package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.soc.SocWars;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.soc.lib.json.JsonHelper.*;
import static com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory.ICON_KEY;
import static net.minecraft.util.JsonHelper.deserialize;

public class EnchantmentUpgradeShopItem implements ShopItem<EnchantmentUpgradeShopItem> {
    public static final int ID = 6;
    private static final PacketCodec<RegistryByteBuf, EnchantmentUpgradeShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(ItemStack.PACKET_CODEC), EnchantmentUpgradeShopItem::getOptionalIcon, PacketCodecs.collection(ArrayList::new, Cost.PACKET_CODEC), EnchantmentUpgradeShopItem::getCosts, Identifier.PACKET_CODEC, EnchantmentUpgradeShopItem::getEnchantment, PacketCodecs.INTEGER, EnchantmentUpgradeShopItem::getTier, EnchantmentUpgradeShopItem::new);

    public static final String COSTS_KEY = "costs";

    private final ItemStack icon;
    private final List<Cost> costs;

    private Either<Identifier, RegistryEntry<Enchantment>> enchantment;

    private int tier;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    public EnchantmentUpgradeShopItem(ItemStack icon, List<Cost> costs, Identifier enchantment, int tier) {
        this.icon = icon;
        this.costs = costs;
        this.enchantment = Either.left(enchantment);
        this.tier = tier;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public EnchantmentUpgradeShopItem(Optional<ItemStack> itemStack, List<Cost> costs, Identifier enchantment, int tier) {
        this(itemStack.orElse(ItemStack.EMPTY), costs, enchantment, tier);
    }

    public EnchantmentUpgradeShopItem(JsonObject object) {
        this(
                getDefaultedItem(object, ICON_KEY, Items.BARRIER.getDefaultStack()).copy(),
                getDefaultedObjectList(object, COSTS_KEY, Cost::new, Optional.of(Cost.ERROR_SIGNAL)),
                Identifier.of(object.get("enchantment").getAsString()),
                0
        );
        this.icon.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    public EnchantmentUpgradeShopItem(Reader reader) {
        this(deserialize(reader));
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        if (this.tier == this.costs.size() || !this.getCost().canAfford(player)) return false;

        this.resolveEnchantment(player.getWorld());
        if (this.enchantment.left().isPresent()) {
            SocWars.LOGGER.warn("Attempted to buy enchantment upgrade but no enchantment was found for id: {}", this.enchantment.left().get());
            return false;
        }

        final RegistryEntry<Enchantment> enchantment = this.enchantment.right().get();

        this.takeItems(player);
        context.refreshItems();
        this.tier++;

        if (player instanceof ServerPlayerEntity serverPlayer) context.getManager().buyEnchantmentUpgrade(serverPlayer, enchantment, this.tier);

        return true;
    }

    private void resolveEnchantment(World world) {
        final Registry<Enchantment> enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        this.enchantment.ifLeft(id -> enchantmentRegistry.getEntry(id).ifPresentOrElse(enchantment -> this.enchantment = Either.right(enchantment), () -> SocWars.LOGGER.warn("Failed to resolve enchantment for shopItem with ID: {}", id)));
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public Cost getCost() {
        return this.tier < this.costs.size() ? this.costs.get(this.tier) : this.costs.getLast();
    }

    private List<Cost> getCosts() {
        return this.costs;
    }

    @Override
    public PacketCodec<RegistryByteBuf, EnchantmentUpgradeShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public EnchantmentUpgradeShopItem lazyClone() {
        return new EnchantmentUpgradeShopItem(this.icon, this.costs, getEnchantment(), 0);
    }

    @Override
    public void enchant(RegistryEntry<Enchantment> enchantment, int tier) {}

    private Identifier getEnchantment() {
        return this.enchantment.map(Function.identity(), entry -> entry.getKey().get().getValue());
    }

    private Optional<ItemStack> getOptionalIcon() {
        return this.getIcon().isEmpty() ? Optional.empty() : Optional.of(this.getIcon());
    }

    private Integer getTier() {
        return this.tier;
    }
}
