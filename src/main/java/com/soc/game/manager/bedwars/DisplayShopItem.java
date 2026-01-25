package com.soc.game.manager.bedwars;

import com.google.gson.JsonObject;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.io.Reader;
import java.util.Optional;

import static com.soc.lib.json.JsonHelper.getDefaultedItem;
import static com.soc.lib.json.JsonHelper.getDefaultedObject;
import static net.minecraft.util.JsonHelper.deserialize;

public class DisplayShopItem implements ShopItem<DisplayShopItem> {
    public static final int ID = 4;
    private static final PacketCodec<RegistryByteBuf, DisplayShopItem> PACKET_CODEC = PacketCodec.tuple(Cost.PACKET_CODEC, DisplayShopItem::getCost, PacketCodecs.optional(ItemStack.PACKET_CODEC), DisplayShopItem::getOptionalIcon, DisplayShopItem::new);

    public static final DisplayShopItem EMPTY = new DisplayShopItem(Cost.DEFAULT, ItemStack.EMPTY);

    private final Cost cost;
    private final ItemStack icon;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public DisplayShopItem(Cost cost, Optional<ItemStack> icon) {
        this.cost = cost;
        this.icon = icon.orElse(ItemStack.EMPTY);
    }

    public DisplayShopItem(Cost cost, ItemStack icon) {
        this.cost = cost;
        this.icon = icon;
    }

    public DisplayShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedItem(object)
        );
    }

    public DisplayShopItem(Reader reader) {
        this(
                deserialize(reader)
        );
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        return false;
    }

    private Optional<ItemStack> getOptionalIcon() {
        return this.icon.isEmpty() ? Optional.empty() : Optional.of(this.icon);
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public Cost getCost() {
        return this.cost;
    }

    @Override
    public PacketCodec<RegistryByteBuf, DisplayShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public DisplayShopItem lazyClone() {
        return this;
    }
}
