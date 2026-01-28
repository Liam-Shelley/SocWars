package com.soc.game.manager.bedwars.shopitems;

import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DisplayShopItem implements ShopItem<DisplayShopItem> {
    public static final int ID = 4;
    private static final PacketCodec<RegistryByteBuf, DisplayShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(ItemStack.PACKET_CODEC), DisplayShopItem::getOptionalIcon, TextCodecs.OPTIONAL_PACKET_CODEC, DisplayShopItem::getOptionalName, TextCodecs.OPTIONAL_PACKET_CODEC, DisplayShopItem::getOptionalTooltip, DisplayShopItem::new);

    private final ItemStack icon;
    @Nullable private final Text name;
    @Nullable private final Text tooltip;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public DisplayShopItem(Optional<ItemStack> icon, Optional<Text> name, Optional<Text> tooltip) {
        this.icon = icon.orElse(ItemStack.EMPTY);
        this.name = name.orElse(null);
        this.tooltip = tooltip.orElse(null);
    }

    public DisplayShopItem(ItemStack icon) {
        this.icon = icon;
        this.name = null;
        this.tooltip = null;
    }

    public DisplayShopItem(ItemStack icon, @Nullable Text name, @Nullable Text tooltip) {
        this.icon = icon;
        this.name = name;
        this.tooltip = tooltip;
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
    public Text getDisplayName() {
        return this.name == null ? this.icon.toHoverableText() : this.name;
    }

    @Nullable
    public Text getTooltip() {
        return this.tooltip;
    }

    private Optional<Text> getOptionalName() {
        return Optional.ofNullable(this.name);
    }

    private Optional<Text> getOptionalTooltip() {
        return Optional.ofNullable(this.tooltip);
    }

    public boolean isEmpty() {
        return this.icon.isEmpty() && this.name == null && this.tooltip == null;
    }
}
