package com.soc.game.manager.bedwars;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BedwarsShopCategory {
    public static final PacketCodec<RegistryByteBuf, BedwarsShopCategory> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, BedwarsShopCategory::isQuickBuy, new PacketCodec<>() {
        @Override
        public List<ShopItem<?>> decode(RegistryByteBuf byteBuf) {
            int size = PacketCodecs.readCollectionSize(byteBuf, Integer.MAX_VALUE);
            final List<ShopItem<?>> collection = new ArrayList<>(Math.min(size, 65536));

            for (int i = 0; i < size; i++) {
                final int itemId = VarInts.read(byteBuf);
                collection.add((ShopItem<?>) ShopItem.DECODER_MAP.get(itemId).apply(byteBuf));
            }

            return collection;
        }

        @Override
        public void encode(RegistryByteBuf byteBuf, List<ShopItem<?>> collection) {
            PacketCodecs.writeCollectionSize(byteBuf, collection.size(), Integer.MAX_VALUE);

            collection.forEach(shopItem -> {
                shopItem.writePacketData(byteBuf);
            });
        }
    }, BedwarsShopCategory::getItems, PacketCodecs.optional(ItemStack.PACKET_CODEC), BedwarsShopCategory::getOptionalIcon, TextCodecs.PACKET_CODEC, BedwarsShopCategory::getName, BedwarsShopCategory::new);

    private final List<ShopItem<?>> items;
    private final ItemStack icon;
    private final Text name;

    private final boolean isQuickBuy;

    public static final BedwarsShopCategory DEFAULT_QUICK_BUY = new BedwarsShopCategory(true, null, Items.NETHER_STAR.getDefaultStack(), Text.translatable("game.bedwars.shop.category.quick_buy"));

    public BedwarsShopCategory(boolean isQuickBuy, List<ShopItem<?>> items, ItemStack icon, Text name) {
        this.items = isQuickBuy ? List.of() : items;
        this.icon = icon.copyWithCount(1);
        this.name = name;
        this.isQuickBuy = isQuickBuy;
    }

    public BedwarsShopCategory(boolean isQuickBuy, List<ShopItem<?>> items, Optional<ItemStack> icon, Text name) {
        this(isQuickBuy, items, icon.orElse(Items.BARRIER.getDefaultStack()), name);
    }

    public BedwarsShopCategory(List<ShopItem<?>> items, ItemStack icon, Text name) {
        this(false, items, icon, name);
    }

    public ShopItem<?> getShopItem(int slot) {
        if (slot < 0 || slot >= this.items.size()) return SimpleShopItem.EMPTY;
        final ShopItem<?> item = this.items.get(slot);
        return item == null ? SimpleShopItem.EMPTY : item;
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

    public List<ShopItem<?>> getItems() {
        List<ShopItem<?>> a = this.getQuickBuyItems();
        return this.isQuickBuy ? a : this.items;
    }

    public List<ShopItem<?>> getQuickBuyItems() {
        return List.of();
    }

    public boolean isQuickBuy() {
        return this.isQuickBuy;
    }

    public void downgradeItems() {
        this.items.forEach(item -> {
            if (item instanceof UpgradeableShopItem upgradeableItem) upgradeableItem.downgrade();
        });
    }
}
