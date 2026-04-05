package com.soc.game.manager.bedwars;

import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.game.manager.bedwars.shopitems.SimpleShopItem;
import com.soc.game.manager.bedwars.shopitems.UpgradeableShopItem;
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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BedwarsShopCategory {
    public static final PacketCodec<RegistryByteBuf, BedwarsShopCategory> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, BedwarsShopCategory::isQuickBuy, new PacketCodec<>() {
        @Override
        public List<ShopItem<?>> decode(RegistryByteBuf byteBuf) {
            final int size = PacketCodecs.readCollectionSize(byteBuf, Integer.MAX_VALUE);
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

            collection.forEach(shopItem -> shopItem.writePacketData(byteBuf));
        }
    }, BedwarsShopCategory::getItems, PacketCodecs.optional(ItemStack.PACKET_CODEC), BedwarsShopCategory::getOptionalIcon, TextCodecs.PACKET_CODEC, BedwarsShopCategory::getName, BedwarsShopCategory::new);

    private final List<ShopItem<?>> items;
    private final ItemStack icon;
    private final Text name;

    private final boolean isQuickBuy;

    public static final BedwarsShopCategory DEFAULT_QUICK_BUY = new BedwarsShopCategory(true, List.of(), Items.NETHER_STAR.getDefaultStack(), Text.translatable("game.bedwars.shop.category.quick_buy"));
    public static final BedwarsShopCategory EMPTY = new BedwarsShopCategory(false, List.of(), ItemStack.EMPTY, Text.empty());

    public BedwarsShopCategory(boolean isQuickBuy, List<ShopItem<?>> items, ItemStack icon, Text name) {
        this.items = isQuickBuy ? List.of() : items;
        this.icon = icon.copyWithCount(1);
        this.name = name;
        this.isQuickBuy = isQuickBuy;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public BedwarsShopCategory(boolean isQuickBuy, List<ShopItem<?>> items, Optional<ItemStack> icon, Text name) {
        this(isQuickBuy, items, icon.orElse(Items.BARRIER.getDefaultStack()), name);
    }

    public BedwarsShopCategory(List<ShopItem<?>> items, ItemStack icon, Text name) {
        this(false, items, icon, name);
    }

    public BedwarsShopCategory(List<ShopItem<?>> items) {
        this(false, items, ItemStack.EMPTY, Text.empty());
    }

    public ShopItem<?> getShopItem(int slot) {
        if (slot < 0 || slot >= this.items.size()) return SimpleShopItem.EMPTY;
        final ShopItem<?> item = this.items.get(slot);
        return item == null ? SimpleShopItem.EMPTY : item;
    }

    public void setShopItem(int slot, ShopItem<?> item) {
        if (slot < 0 || slot >= this.items.size()) return;
        this.items.set(slot, item);
    }

    public void forEach(Consumer<ShopItem<?>> function) {
        this.items.forEach(function);
    }

    public void forEachEnumerate(BiConsumer<Integer, ShopItem<?>> function) {
        for (int i = 0; i < this.items.size(); i++) {
            function.accept(i, this.items.get(i));
        }
    }

    /// Return false to early exit
    public void forEachEnumerate(BiFunction<Integer, ShopItem<?>, Boolean> function) {
        for (int i = 0; i < this.items.size(); i++) {
            if (!function.apply(i, this.items.get(i))) return;
        }
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
        return this.isQuickBuy ? this.getQuickBuyItems() : this.items;
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

    public boolean hasEmptySlot() {
        for (ShopItem<?> item : this.items) {
            if (item.getIcon().isEmpty()) return true;  //Should probably make some actual way to check if a shop item is empty but this should do for now
        }
        return false;
    }

    public int size() {
        return this.items.size();
    }
}
