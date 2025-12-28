package com.soc.game.manager.bedwars;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BedwarsShopCategory {
    public static final int MAX_ITEMS = 36;
    public static final PacketCodec<RegistryByteBuf, BedwarsShopCategory> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, BedwarsShopCategory::isQuickBuy, PacketCodecs.collection(ArrayList::new, BaseShopItem.PACKET_CODEC), BedwarsShopCategory::getItems, PacketCodecs.optional(ItemStack.PACKET_CODEC), BedwarsShopCategory::getOptionalIcon, TextCodecs.PACKET_CODEC, BedwarsShopCategory::getName, BedwarsShopCategory::new);

    private final List<BaseShopItem> items;
    private final ItemStack icon;
    private final Text name;

    private final boolean isQuickBuy;

    public static final BedwarsShopCategory DEFAULT_QUICK_BUY = new BedwarsShopCategory(true, null, Items.NETHER_STAR.getDefaultStack(), Text.translatable("game.bedwars.shop.category.quick_buy"));

    public BedwarsShopCategory(boolean isQuickBuy, List<BaseShopItem> items, ItemStack icon, Text name) {
        this.items = isQuickBuy ? List.of() : items;
        this.icon = icon.copyWithCount(1);
        this.name = name;
        this.isQuickBuy = isQuickBuy;
    }

    public BedwarsShopCategory(boolean isQuickBuy, List<BaseShopItem> items, Optional<ItemStack> icon, Text name) {
        this(isQuickBuy, items, icon.orElse(Items.BARRIER.getDefaultStack()), name);
    }

    public BedwarsShopCategory(List<BaseShopItem> items, ItemStack icon, Text name) {
        this(false, items, icon, name);
    }

    public BaseShopItem getShopItem(int slot) {
        if (slot < 0 || slot >= this.items.size()) return BaseShopItem.EMPTY;
        final BaseShopItem item = this.items.get(slot);
        return item == null ? BaseShopItem.EMPTY : item;
    }

    public ItemStack getIcon() {
        return this.icon;
    }
    private Optional<ItemStack> getOptionalIcon() {
        return this.icon.isEmpty() ? Optional.empty() : Optional.of(this.icon);
    }

    public Text getName() {
        return this.name;
    }

    public List<BaseShopItem> getItems() {
        List<BaseShopItem> a = this.getQuickBuyItems();
        return this.isQuickBuy ? a : this.items;
    }

    public List<BaseShopItem> getQuickBuyItems() {
        return List.of();
    }

    public boolean isQuickBuy() {
        return this.isQuickBuy;
    }
}
