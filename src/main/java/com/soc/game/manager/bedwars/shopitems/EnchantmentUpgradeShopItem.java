package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.soc.lib.json.JsonHelper.*;
import static com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory.ICON_KEY;
import static net.minecraft.util.JsonHelper.deserialize;

public class EnchantmentUpgradeShopItem implements ShopItem<EnchantmentUpgradeShopItem> {
    public static final int ID = 6;
    private static final PacketCodec<RegistryByteBuf, EnchantmentUpgradeShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(ItemStack.PACKET_CODEC), EnchantmentUpgradeShopItem::getOptionalIcon, PacketCodecs.collection(ArrayList::new, Cost.PACKET_CODEC), EnchantmentUpgradeShopItem::getCosts, PacketCodecs.INTEGER, EnchantmentUpgradeShopItem::getTier, EnchantmentUpgradeShopItem::new);

    public static final String COSTS_KEY = "costs";

    private final ItemStack icon;
    private final List<Cost> costs;

    private int tier;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    public EnchantmentUpgradeShopItem(ItemStack icon, List<Cost> costs, int tier) {
        this.icon = icon;
        this.costs = costs;
        this.tier = tier;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public EnchantmentUpgradeShopItem(Optional<ItemStack> itemStack, List<Cost> costs, Integer tier) {
        this(itemStack.orElse(ItemStack.EMPTY), costs, tier);
    }

    public EnchantmentUpgradeShopItem(JsonObject object) {
        this(
                getDefaultedItem(object, ICON_KEY, Items.BARRIER.getDefaultStack()),
                getDefaultedObjectList(object, COSTS_KEY, Cost::new, Optional.of(Cost.ERROR_SIGNAL)),
                0
        );
    }

    public EnchantmentUpgradeShopItem(Reader reader) {
        this(deserialize(reader));
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        if (this.tier == this.costs.size()) return false;

        boolean gaveStack;
        if (this.tier == 0) {
            gaveStack = true;
        } else {
            gaveStack = true;
        }

        if (gaveStack) {
            this.tier++;
            this.takeItems(player);
            context.refreshItems();
        }

        return gaveStack;
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
        return new EnchantmentUpgradeShopItem(this.icon, this.costs, 0);
    }

    private Optional<ItemStack> getOptionalIcon() {
        return this.getIcon().isEmpty() ? Optional.empty() : Optional.of(this.getIcon());
    }

    private int getTier() {
        return this.tier;
    }
}
