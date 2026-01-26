package com.soc.game.manager.bedwars;

import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.game.manager.bedwars.shopitems.UpgradeableShopItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record BedwarsShopContents(List<BedwarsShopCategory> contents) {
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

    public void downgradeItems() {
        this.contents.forEach(BedwarsShopCategory::downgradeItems);
    }

    /// Will crash if you try to use this with the wrong id for the type of shop item
    @SuppressWarnings("unchecked")
    public <T extends ShopItem<T>> List<T> getShopItemsByTypeId(int id) {
        return (List<T>)(Object)this.contents.stream().flatMap(category -> category.getItems().stream().filter(item -> item.id() == id)).toList();
    }

    public Optional<UpgradeableShopItem> getUpgradeableShopItemBySlotTrackingId(int id) {
        return this.<UpgradeableShopItem>getShopItemsByTypeId(UpgradeableShopItem.ID).stream().filter(item -> item.matchesSlotTrackingId(id)).findFirst();
    }

    public void setCategory(int index, BedwarsShopCategory category) {
        if (index >= this.contents.size()) {
            this.contents.add(category);
        } else {
            this.contents.set(index, category);
        }
    }
}