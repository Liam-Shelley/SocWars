package com.soc.game.manager.bedwars;

import com.soc.game.manager.BedwarsGameManager;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public abstract class BaseShopItem {
    public static final PacketCodec<RegistryByteBuf, BaseShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER), BaseShopItem::getCosts, PacketCodecs.optional(ItemStack.PACKET_CODEC), BaseShopItem::getOptionalIcon, ClientDisplayShopItem::new);

    private static class ClientDisplayShopItem extends BaseShopItem {
        private ClientDisplayShopItem(List<Integer> costs, Optional<ItemStack> icon) {
            super(costs.get(0), costs.get(1), costs.get(2), costs.get(3), icon.orElse(ItemStack.EMPTY));
        }

        @Override
        public boolean buy(PlayerEntity player, BedwarsShopScreenHandler context) {
            return false;
        }
    }

    public static final BaseShopItem EMPTY = new BaseShopItem(1, 1, 1, 1, Items.AIR.getDefaultStack()) {
        @Override
        public boolean buy(PlayerEntity player, BedwarsShopScreenHandler context) {
            return false;
        }
    };

    protected final Map<Item, Integer> costMap;
    protected final ItemStack icon;

    protected BaseShopItem(int iron, int gold, int diamonds, int emeralds, ItemStack icon) {
        this.costMap = new LinkedHashMap<>(4);
        this.costMap.put(Items.IRON_INGOT, iron);
        this.costMap.put(Items.GOLD_INGOT, gold);
        this.costMap.put(Items.DIAMOND, diamonds);
        this.costMap.put(Items.EMERALD, emeralds);

        this.icon = icon;
    }

    protected BaseShopItem(Cost cost, ItemStack icon) {
        this(cost.iron(), cost.gold(), cost.diamonds(), cost.emeralds(), icon);
    }

    public abstract boolean buy(PlayerEntity player, BedwarsShopScreenHandler context);

    public Text getTooltipName() {
        return getTooltipNameOfItem(this.icon);
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
    protected final BedwarsGameManager getManager(PlayerEntity player) {
        return BedwarsGameManager.getBedwarsGameManager(player);
    }

    public final Map<Item, Integer> getCostMap() {
        return this.costMap;
    }

    public final ItemStack getIcon() {
        return this.icon;
    }
    public final Optional<ItemStack> getOptionalIcon() {
        return this.icon.isEmpty() ? Optional.empty() : Optional.of(this.icon);
    }

    private List<Integer> getCosts() {
        return List.copyOf(this.costMap.values());
    }
}
