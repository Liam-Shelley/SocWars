package com.soc.game.manager.bedwars;

import com.google.gson.JsonObject;
import com.soc.items.TrainingWeights;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static com.soc.lib.SocWarsLib.enchantment;
import static com.soc.lib.json.JsonHelper.*;
import static net.minecraft.util.JsonHelper.deserialize;

public class SimpleShopItem extends BaseShopItem<SimpleShopItem> {
    private static final int ID = 1;

    static {
        BaseShopItem.DECODER_MAP.put(ID, SimpleShopItem::decode);
    }

    private static final PacketCodec<RegistryByteBuf, SimpleShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER), SimpleShopItem::getCosts, PacketCodecs.optional(ItemStack.PACKET_CODEC), SimpleShopItem::getOptionalStack, SimpleShopItem::new);

    public static final SimpleShopItem EMPTY = new SimpleShopItem(Cost.DEFAULT, ItemStack.EMPTY);


    private final ItemStack stack;

    private SimpleShopItem(List<Integer> costs, Optional<ItemStack> stack) {
        super(costs.get(0), costs.get(1), costs.get(2), costs.get(3));
        this.stack = stack.orElse(ItemStack.EMPTY);
    }

    public SimpleShopItem(Cost cost, ItemStack stack) {
        super(cost);
        this.stack = stack;
    }

    public SimpleShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedItem(object)
        );
    }

    public SimpleShopItem(Reader reader) {
        this(
                deserialize(reader)
        );
    }

    @Override
    public boolean buy(PlayerEntity player, BedwarsShopScreenHandler context) {
        final Pair<Boolean, OptionalInt> canAfford = super.canAfford(player);
        if (!canAfford.getLeft()) return false;

        super.costMap.forEach((item, count) -> Inventories.remove(player.getInventory(), predStack -> predStack.isOf(item), count, false));

        final EquippableComponent equippableComponent = this.stack.get(DataComponentTypes.EQUIPPABLE);

        if (equippableComponent != null && equippableComponent.slot().isArmorSlot()) {
            final ItemStack stack = this.stack.copy();
            if (!this.stack.isOf(TrainingWeights.TRAINING_WEIGHTS)) stack.addEnchantment(enchantment(player.getWorld(), Enchantments.BINDING_CURSE), 1);
            player.equipStack(equippableComponent.slot(), stack);
            return true;
        } else {
            final boolean openSlot = canAfford.getRight().isPresent();

            if (openSlot) player.giveItemStack(this.stack.copy());
            return openSlot;
        }
    }

    private Optional<ItemStack> getOptionalStack() {
        return this.stack.isEmpty() ? Optional.empty() : Optional.of(this.stack);
    }

    @Override
    public Text getTooltipName() {
        return getTooltipNameOfItem(this.stack);
    }

    @Override
    public ItemStack getIcon() {
        return this.stack;
    }

    @Override
    protected PacketCodec<RegistryByteBuf, SimpleShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    protected int id() {
        return ID;
    }

    private static SimpleShopItem decode(RegistryByteBuf byteBuf) {
        return PACKET_CODEC.decode(byteBuf);
    }
}
