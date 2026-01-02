package com.soc.game.manager.bedwars;

import com.soc.items.TrainingWeights;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.*;
import java.util.function.Function;

import static com.soc.lib.SocWarsLib.enchantment;
import static com.soc.lib.SocWarsLib.inventoryCanAcceptStack;

public interface ShopItem<INHERITOR> {
    Map<Integer, Function<RegistryByteBuf, ?>> DECODER_MAP = new HashMap<>();

    boolean buy(PlayerEntity player, BedwarsShopScreenHandler context);

    ItemStack getIcon();

    Cost getCost();

    PacketCodec<RegistryByteBuf, INHERITOR> getPacketCodec();

    default void takeItems(PlayerEntity player) {
        this.getCost().takeItems(player, false);
    }

    int id();

    @MustBeInvokedByOverriders
    @SuppressWarnings("unchecked")
    default void writePacketData(RegistryByteBuf byteBuf) {
        VarInts.write(byteBuf, this.id());
        this.getPacketCodec().encode(byteBuf, (INHERITOR)this);
    }

    default Text getTooltipName() {
        return getTooltipNameOfItem(this.getIcon());
    }

    static Text getTooltipNameOfItem(ItemStack icon) {
        return Text.literal(icon.getCount() + "x ").formatted(Formatting.DARK_PURPLE).append(icon.getItemName().copy().formatted(icon.getRarity().getFormatting()));
    }

    default boolean giveStack(ItemStack itemStack, PlayerEntity player, boolean checkSlot) {
        if (!this.getCost().canAfford(player)) return false;

        final EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);

        if (equippableComponent != null && equippableComponent.slot().isArmorSlot()) {
            if (ItemStack.areItemsEqual(player.getEquippedStack(equippableComponent.slot()), itemStack)) return false;

            final ItemStack stack = itemStack.copy();

            if (!stack.isOf(TrainingWeights.TRAINING_WEIGHTS)) stack.addEnchantment(enchantment(player.getWorld(), Enchantments.BINDING_CURSE), 1);
            player.equipStack(equippableComponent.slot(), stack);
            return true;
        } else {
            if (inventoryCanAcceptStack(player.getInventory(), itemStack) || !checkSlot) {
                player.giveItemStack(itemStack.copy());
                return true;
            }

            return false;
        }
    }

    default Text affordabilitySuffix(PlayerEntity player) {
        return this.getCost().canAfford(player) ? Text.literal(" ✔").formatted(Formatting.GREEN) : Text.empty();
    }

    /// Inheritors with mutable data should return a deeper clone, anything immutable can simply return a reference to itself
    INHERITOR lazyClone();
}
