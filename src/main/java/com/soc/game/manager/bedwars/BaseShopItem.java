package com.soc.game.manager.bedwars;

import com.soc.game.manager.BedwarsGameManager;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;

public abstract class BaseShopItem<PACKET> {


    //public static final PacketCodec<RegistryByteBuf, BaseShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER), BaseShopItem::getCosts, PacketCodecs.optional(ItemStack.PACKET_CODEC), BaseShopItem::getOptionalIcon, ClientDisplayShopItem::new);

    /*
    private static class ClientDisplayShopItem extends BaseShopItem {
        private final ItemStack icon;

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private ClientDisplayShopItem(List<Integer> costs, Optional<ItemStack> icon) {
            super(costs.get(0), costs.get(1), costs.get(2), costs.get(3));
            this.icon = icon.orElse(ItemStack.EMPTY);
        }

        @Override
        public boolean buy(PlayerEntity player, BedwarsShopScreenHandler context) {
            return false;
        }

        @Override
        public ItemStack getIcon() {
            return this.icon;
        }
    }
     */

    public static final Map<Integer, Function<RegistryByteBuf, ?>> DECODER_MAP = new HashMap<>();

    protected final Map<Item, Integer> costMap;

    protected BaseShopItem(int iron, int gold, int diamonds, int emeralds) {
        this.costMap = new LinkedHashMap<>(4);
        this.costMap.put(Items.IRON_INGOT, iron);
        this.costMap.put(Items.GOLD_INGOT, gold);
        this.costMap.put(Items.DIAMOND, diamonds);
        this.costMap.put(Items.EMERALD, emeralds);
    }

    protected BaseShopItem(Cost cost) {
        this(cost.iron(), cost.gold(), cost.diamonds(), cost.emeralds());
    }

    public abstract boolean buy(PlayerEntity player, BedwarsShopScreenHandler context);

    public Text getTooltipName() {
        return getTooltipNameOfItem(this.getIcon());
    }

    protected static Text getTooltipNameOfItem(ItemStack icon) {
        return Text.literal(icon.getCount() + "x ").formatted(Formatting.DARK_PURPLE).append(icon.getItemName().copy().formatted(icon.getRarity().getFormatting()));
    }

    public final Pair<Boolean, OptionalInt> canAfford(PlayerEntity player) {
        final PlayerInventory playerInventory = player.getInventory();

        final boolean canAfford = this.costMap.entrySet().stream().map(entry -> playerInventory.count(entry.getKey()) >= entry.getValue()).reduce(true, Boolean::logicalAnd);
        final int emptySlot = playerInventory.getEmptySlot();

        return Pair.of(canAfford, emptySlot >= 0 ? OptionalInt.of(emptySlot) : OptionalInt.empty());
    }

    //Just manually inline this once I actually use it
    protected static BedwarsGameManager getManager(PlayerEntity player) {
        return BedwarsGameManager.getBedwarsGameManager(player);
    }

    public final Map<Item, Integer> getCostMap() {
        return this.costMap;
    }

    public abstract ItemStack getIcon();
    public final Optional<ItemStack> getOptionalIcon() {
        return this.getIcon().isEmpty() ? Optional.empty() : Optional.of(this.getIcon());
    }

    protected final List<Integer> getCosts() {
        return List.copyOf(this.costMap.values());
    }

    protected void takeItems(ServerPlayerEntity player) {
        this.costMap.forEach((item, count) -> Inventories.remove(player.getInventory(), predStack -> predStack.isOf(item), count, false));
    }

    protected abstract PacketCodec<RegistryByteBuf, PACKET> getPacketCodec();
    protected abstract int id();

    public final void writePacketData(RegistryByteBuf byteBuf) {
        this.getPacketCodec().encode(byteBuf, (PACKET)this);
    }
}
