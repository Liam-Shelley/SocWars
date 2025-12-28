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
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Reader;
import java.util.OptionalInt;

import static com.soc.lib.SocWarsLib.enchantment;
import static com.soc.lib.json.JsonHelper.*;
import static net.minecraft.util.JsonHelper.deserialize;

public class SimpleShopItem extends BaseShopItem {
    private final ItemStack stack;

    public SimpleShopItem(int iron, int gold, int diamonds, int emeralds, ItemStack stack) {
        super(iron, gold, diamonds, emeralds, stack);
        this.stack = stack;
    }

    public SimpleShopItem(Cost cost, ItemStack stack) {
        super(cost, stack);
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

    @Override
    public Text getTooltipName() {
        return getTooltipNameOfItem(this.stack);
    }
}
