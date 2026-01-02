package com.soc.game.manager.bedwars;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soc.items.components.ModComponents;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.resourcedata.deserialisation.CostStack;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.lib.json.JsonHelper.getDefaultedBoolean;
import static net.minecraft.util.JsonHelper.deserialize;

public class UpgradeableShopItem implements ShopItem<UpgradeableShopItem> {
    public static final int ID = 2;
    private static final PacketCodec<RegistryByteBuf, UpgradeableShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, CostStack.PACKET_CODEC), UpgradeableShopItem::getStacks, PacketCodecs.BOOLEAN, UpgradeableShopItem::downgradeOnDeath, PacketCodecs.BOOLEAN, UpgradeableShopItem::retainBaseTier, PacketCodecs.INTEGER, UpgradeableShopItem::getTier, UpgradeableShopItem::new);

    static {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    private static final AtomicInteger SLOT_TRACKING_ID_TRACKER = new AtomicInteger();

    public static final String TIERS_KEY = "tiers";
    public static final String DOWNGRADE_ON_DEATH_KEY = "downgrade_on_death";
    public static final String RETAIN_BASE_TIER_KEY = "retain_base_tier";

    private final List<CostStack> stacks;
    private final boolean downgradeOnDeath;

    private final boolean retainBaseTier;
    private final int slotTrackingId = SLOT_TRACKING_ID_TRACKER.getAndIncrement();

    private int tier;

    private UpgradeableShopItem(List<CostStack> stacks, boolean downgradeOnDeath, boolean retainBaseTier, int tier) {
        this.stacks = stacks;
        this.downgradeOnDeath = downgradeOnDeath;
        this.retainBaseTier = retainBaseTier;
        this.tier = tier;
    }

    public UpgradeableShopItem(JsonObject object) {
        this(
                deserialiseItems(JsonHelper.getArray(object, TIERS_KEY)),
                getDefaultedBoolean(object, DOWNGRADE_ON_DEATH_KEY),
                getDefaultedBoolean(object, RETAIN_BASE_TIER_KEY),
                0
        );
    }

    public UpgradeableShopItem(Reader reader) {
        this(deserialize(reader));
    }

    private static List<CostStack> deserialiseItems(JsonArray array) {
        final List<CostStack> items = new ArrayList<>();
        array.forEach(element -> items.add(new CostStack(element.getAsJsonObject())));

        return items;
    }

    @Override
    public boolean buy(PlayerEntity player, BedwarsShopScreenHandler context) {
        if (this.tier == this.stacks.size()) return false;

        boolean gaveStack;
        if (this.tier == 0) {
            gaveStack = this.giveStack(this.getStack(), player, OptionalInt.empty(), stack -> stack.set(ModComponents.GAME_TOOL, this.slotTrackingId));
        } else {
            final PlayerInventory inventory = player.getInventory();

            OptionalInt slot = OptionalInt.empty();
            for (int i = 0; i < PlayerInventory.MAIN_SIZE; i++) {
                final Integer component = inventory.getStack(i).get(ModComponents.GAME_TOOL);
                if (component != null && component == this.slotTrackingId) {
                    slot = OptionalInt.of(i);
                    break;
                }
            }

            gaveStack = this.giveStack(this.getStack(), player, slot, stack -> stack.set(ModComponents.GAME_TOOL, this.slotTrackingId));
        }

        if (gaveStack) {
            this.tier++;
            this.takeItems(player);
            context.refreshItems();
        }

        return gaveStack;
    }

    private List<CostStack> getStacks() {
        return this.stacks;
    }
    private boolean downgradeOnDeath() {
        return this.downgradeOnDeath;
    }
    private boolean retainBaseTier() {
        return this.retainBaseTier;
    }
    private int getTier() {
        return this.tier;
    }

    private CostStack getStackAndCost() {
        return this.tier < this.stacks.size() ? this.stacks.get(this.tier) : this.stacks.getLast();
    }

    private ItemStack getStack() {
        return this.getStackAndCost().stack();
    }

    public ItemStack getDowngradedStackCopy() {
        if (this.tier == 0) {
            return ItemStack.EMPTY;
        } else {
            final ItemStack stack = this.stacks.get(this.tier - 1).stack().copy();
            stack.set(ModComponents.GAME_TOOL, this.slotTrackingId);
            return stack;
        }
    }

    @Override
    public ItemStack getIcon() {
        return this.getStack();
    }

    @Override
    public Cost getCost() {
        return this.getStackAndCost().cost();
    }

    @Override
    public PacketCodec<RegistryByteBuf, UpgradeableShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    public int getSlotTrackingId() {
        return this.slotTrackingId;
    }

    public boolean matchesSlotTrackingId(int id) {
        return this.slotTrackingId == id;
    }

    public void downgrade() {
        if (this.downgradeOnDeath && this.tier > (this.retainBaseTier ? 1 : 0)) this.tier--;
    }

    @Override
    public Text affordabilitySuffix(PlayerEntity player) {
        return this.tier < this.stacks.size() ? ShopItem.super.affordabilitySuffix(player) : Text.translatable("game.bedwars.shop.item.max_tier").formatted(Formatting.YELLOW, Formatting.BOLD);
    }

    @Override
    public UpgradeableShopItem lazyClone() {
        return new UpgradeableShopItem(this.stacks, this.downgradeOnDeath, this.retainBaseTier, 0);
    }
}
