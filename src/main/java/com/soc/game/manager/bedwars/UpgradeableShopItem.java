package com.soc.game.manager.bedwars;

import com.google.gson.JsonObject;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import static com.soc.lib.json.JsonHelper.getDefaultedObject;
import static net.minecraft.util.JsonHelper.deserialize;

public class UpgradeableShopItem extends BaseShopItem<UpgradeableShopItem> {
    private static final int ID = 2;

    static {
        BaseShopItem.DECODER_MAP.put(ID, UpgradeableShopItem::decode);
    }

    private static final PacketCodec<RegistryByteBuf, UpgradeableShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER), UpgradeableShopItem::getCosts, PacketCodecs.collection(ArrayList::new, ItemStack.PACKET_CODEC), UpgradeableShopItem::getStacks, PacketCodecs.INTEGER, UpgradeableShopItem::getTier, UpgradeableShopItem::new);

    private final List<ItemStack> stacks;
    private int tier;

    private UpgradeableShopItem(List<Integer> costs, List<ItemStack> stacks, int tier) {
        super(costs.get(0), costs.get(1), costs.get(2), costs.get(3));
        this.stacks = stacks;
        this.tier = tier;
    }

    public UpgradeableShopItem(Cost cost, List<ItemStack> stacks) {
        super(cost);
        this.stacks = stacks;
    }

    public UpgradeableShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                List.of(Items.EMERALD_BLOCK.getDefaultStack())
        );
    }

    public UpgradeableShopItem(Reader reader) {
        this(
                deserialize(reader)
        );
    }

    @Override
    public boolean buy(PlayerEntity player, BedwarsShopScreenHandler context) {
        final Pair<Boolean, OptionalInt> canAfford = super.canAfford(player);
        if (!canAfford.getLeft()) return false;

        super.costMap.forEach((item, count) -> Inventories.remove(player.getInventory(), predStack -> predStack.isOf(item), count, false));

        final EquippableComponent equippableComponent = this.getStack().get(DataComponentTypes.EQUIPPABLE);

        if (equippableComponent != null && equippableComponent.slot().isArmorSlot()) {
            player.equipStack(equippableComponent.slot(), this.getStack().copy());
            return true;
        } else {
            final boolean openSlot = canAfford.getRight().isPresent();

            if (openSlot) player.giveItemStack(this.getStack().copy());
            return openSlot;
        }
    }

    private List<ItemStack> getStacks() {
        return this.stacks;
    }

    private int getTier() {
        return this.tier;
    }

    private ItemStack getStack() {
        return this.stacks.get(this.tier);
    }

    @Override
    public Text getTooltipName() {
        return getTooltipNameOfItem(this.getIcon());
    }

    @Override
    public ItemStack getIcon() {
        return this.getStack();
    }

    @Override
    protected PacketCodec<RegistryByteBuf, UpgradeableShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    protected int id() {
        return ID;
    }

    private static UpgradeableShopItem decode(RegistryByteBuf byteBuf) {
        return PACKET_CODEC.decode(byteBuf);
    }
}
