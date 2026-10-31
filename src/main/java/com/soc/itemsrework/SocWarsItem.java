package com.soc.itemsrework;

import com.soc.itemsrework.components.*;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.*;

public class SocWarsItem extends Item { //I wonder if I should just turn this into a mixin for Item
	public SocWarsItem(Settings settings) {
		super(settings);
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.streamAll(ItemHitComponent.class).forEach(itemHit -> itemHit.postHit(stack, target, attacker));
	}

	public void onAttackButtonPressed(PlayerEntity player, ItemStack stack) {
		stack.streamAll(ItemHitAllComponent.class).forEach(itemHitAll -> itemHitAll.hit(player, stack));
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		return user.getStackInHand(hand)
				.streamAll(ItemUseComponent.class)
				.map(itemUse -> itemUse.use(world, user, hand))
				.reduce(super.use(world, user, hand), (a, b) -> b.isAccepted() ? a : a.isAccepted() ? b : a); //Honestly I have no idea how I actually want to handle this
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		final PlayerEntity user = context.getPlayer();
		if (user == null) return super.useOnBlock(context);

		final World world = context.getWorld();
		final Hand hand = context.getHand();
		final BlockPos pos = context.getBlockPos();

		return user.getStackInHand(hand)
				.streamAll(ItemUseOnBlockComponent.class)
				.map(itemUse -> itemUse.use(world, user, hand, world.getBlockState(pos), pos))
				.reduce(super.useOnBlock(context), (a, b) -> b.isAccepted() ? a : a.isAccepted() ? b : a); //Ditto
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ifNotNull(stack.get(ModItemComponentsReworked.FINISH_USE), finishUse -> finishUse.finishUsing(stack, world, user));
		return super.finishUsing(stack, world, user);
	}

	@Override
	public @Nullable DamageSource getDamageSource(LivingEntity user) {
		return mapIfNotNull(user.getStackInHand(Hand.MAIN_HAND).get(ModItemComponentsReworked.DAMAGE_TYPE), damageType -> damageSource(user.getWorld(), damageType, user));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		stack.streamAll(DynamicTooltipComponent.class).forEach(creator -> creator.create(stack, getWorldTime()).forEach(textConsumer));
	}
}
