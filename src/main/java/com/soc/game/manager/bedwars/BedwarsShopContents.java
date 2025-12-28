package com.soc.game.manager.bedwars;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public record BedwarsShopContents(List<BedwarsShopCategory> contents) {
    public static final int MAX_CATEGORIES = 8;
    public static final PacketCodec<RegistryByteBuf, BedwarsShopContents> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, BedwarsShopCategory.PACKET_CODEC), BedwarsShopContents::contents, BedwarsShopContents::new);

    public BedwarsShopCategory getCategory(int slot) {
        if (slot < 0 || slot >= contents.size()) return null;
        return this.contents.get(slot);
    }

    public BedwarsShopCategory getFirstCategory() {
        if (this.contents.isEmpty()) return null;
        return this.contents.getFirst();
    }

    public List<ItemStack> getCategoryIcons() {
        return this.contents.stream().map(BedwarsShopCategory::getIcon).toList();
    }

    public int getNumCategories() {
        return this.contents.size();
    }
}
