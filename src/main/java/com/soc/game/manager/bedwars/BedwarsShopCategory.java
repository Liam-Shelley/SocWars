package com.soc.game.manager.bedwars;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.ArrayList;
import java.util.List;

public class BedwarsShopCategory {
    public static final int MAX_ITEMS = 36;
    public static final PacketCodec<RegistryByteBuf, BedwarsShopCategory> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, BaseShopItem.PACKET_CODEC), BedwarsShopCategory::getItems, ItemStack.PACKET_CODEC, BedwarsShopCategory::getIcon, TextCodecs.PACKET_CODEC, BedwarsShopCategory::getName, BedwarsShopCategory::new);

    private final List<BaseShopItem> items;
    private final ItemStack icon;
    private final Text name;

    public BedwarsShopCategory(ItemStack icon, Text name) {
        this(List.of(new SimpleShopItem(8, 1, 0, 0, new ItemStack(icon.getItem(), 3))), icon, name);
    }

    public BedwarsShopCategory(List<BaseShopItem> items, ItemStack icon, Text name) {
        this.items = items;
        this.icon = icon.copyWithCount(1);
        this.name = name;
    }

    public BaseShopItem getShopItem(int slot) {
        if (slot < 0 || slot >= this.items.size()) return BaseShopItem.EMPTY;
        return this.items.get(slot);
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public Text getName() {
        return this.name;
    }

    public List<BaseShopItem> getItems() {
        return this.items;
    }
}
