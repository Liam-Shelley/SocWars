package com.soc.game.manager.bedwars.shopitems;

import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;

import java.util.Optional;

public class DisplayShopItem implements ShopItem<DisplayShopItem> {
    public static final int ID = 4;
    private static final PacketCodec<RegistryByteBuf, DisplayShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(ItemStack.PACKET_CODEC), DisplayShopItem::getOptionalIcon, DisplayShopItem::new);

    private final ItemStack icon;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public DisplayShopItem(Optional<ItemStack> icon) {
        this.icon = icon.orElse(ItemStack.EMPTY);
    }

    public DisplayShopItem(ItemStack icon) {
        this.icon = icon;
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
        return Cost.DEFAULT;
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

    @Override
    public Text getTooltipName() {
        return Text.of("trap");
    }
}
