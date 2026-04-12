package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.soc.lib.json.JsonHelper.*;
import static net.minecraft.util.JsonHelper.deserialize;

public class EnchantmentUpgradeShopItem extends TieredShopItem<EnchantmentUpgradeShopItem> {
    public static final int ID = 6;
    private static final PacketCodec<RegistryByteBuf, EnchantmentUpgradeShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(ItemStack.PACKET_CODEC), EnchantmentUpgradeShopItem::getOptionalIcon, PacketCodecs.collection(ArrayList::new, Cost.PACKET_CODEC), EnchantmentUpgradeShopItem::getCosts, Identifier.PACKET_CODEC, EnchantmentUpgradeShopItem::getEnchantment, PacketCodecs.INTEGER, EnchantmentUpgradeShopItem::getTier, EnchantmentUpgradeShopItem::new);

    private final ItemStack icon;
    private final Identifier enchantment;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    public EnchantmentUpgradeShopItem(ItemStack icon, List<Cost> costs, Identifier enchantment, int tier) {
        super(costs, tier);
        this.icon = icon;
        this.enchantment = enchantment;
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
        final Optional<RegistryEntry.Reference<Enchantment>> enchantmentOptional = this.resolveEnchantment(player.getWorld());

        if (enchantmentOptional.isEmpty()) {
            SocWars.LOGGER.warn("Attempted to buy enchantment upgrade but no enchantment was found for id: {}", this.enchantment);
            player.sendMessage(Text.literal("Something went wrong while purchasing; your resources have not been taken"), false); //Maybe translate this
            return false;
        }

        if (!super.buy(player, context)) return false;

        if (player instanceof ServerPlayerEntity serverPlayer) context.getManager().buyEnchantmentUpgrade(serverPlayer, enchantmentOptional.get(), this.tier);

        return true;
    }

    private Optional<RegistryEntry.Reference<Enchantment>> resolveEnchantment(World world) {
        final Registry<Enchantment> enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        return enchantmentRegistry.getEntry(this.enchantment);
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
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
        return new EnchantmentUpgradeShopItem(this.icon.copy(), this.costs, this.enchantment, 0);
    }

    private Identifier getEnchantment() {
        return this.enchantment;
    }

    @Override
    protected MutableText getBaseName() {
        return Text.translatable(enchantment.toTranslationKey("enchantment"));
    }
}
