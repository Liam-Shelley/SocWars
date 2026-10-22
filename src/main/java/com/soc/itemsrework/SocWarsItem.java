package com.soc.itemsrework;

import com.soc.items.ThrowableItem;
import com.soc.itemsrework.components.ModItemComponentsReworked;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.*;

public class SocWarsItem extends Item {
	public SocWarsItem(Settings settings) {
		super(settings);
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		ifNotNull(stack.get(ModItemComponentsReworked.ITEM_HIT), itemHit -> itemHit.postHit(stack, target, attacker));
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		return mapIfNotNull(user.getStackInHand(hand).get(ModItemComponentsReworked.ITEM_USE), itemUse -> itemUse.use(world, user, hand), ActionResult.PASS);
	}

	@Override
	public @Nullable DamageSource getDamageSource(LivingEntity user) {
		return mapIfNotNull(user.getStackInHand(Hand.MAIN_HAND).get(ModItemComponentsReworked.DAMAGE_TYPE), damageType -> damageSource(user.getWorld(), damageType, user));
	}

	@Override
	@SuppressWarnings({"deprecation", "SwitchStatementWithTooFewBranches"})
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		switch (stack.getItem().toString()) {
			case "socwars:fleshy_blade" -> textConsumer.accept(Text.translatable(ThrowableItem.getWorldTime() % 25 > 2 ? "tooltip.fleshy_blade" : "tooltip.fleshy_blade.wet").formatted(Formatting.RED));
		}
	}
}
