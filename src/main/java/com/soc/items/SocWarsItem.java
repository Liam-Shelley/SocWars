package com.soc.items;

import com.soc.items.components.ScaledUseDuration;
import com.soc.items.components.*;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
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
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof PlayerEntity player && player.getGameMode() == GameMode.SPECTATOR) return false;

		return stack
				.streamAll(ItemStopUsingComponent.class)
				.map(itemStopUsing -> itemStopUsing.onStoppedUsing(stack, world, user, remainingUseTicks))
				.reduce(false, Boolean::logicalOr);
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
		stack.streamAll(ItemFinishUseComponent.class).forEach(finishUse -> finishUse.finishUsing(stack, world, user));
		return super.finishUsing(stack, world, user);
	}

	@Override
	public @Nullable DamageSource getDamageSource(LivingEntity user) {
		return mapIfNotNull(user.getStackInHand(Hand.MAIN_HAND).get(ModItemComponents.DAMAGE_TYPE), damageType -> damageSource(user.getWorld(), damageType, user), super.getDamageSource(user));
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return stack.getOrDefault(ModItemComponents.USE_ACTION, super.getUseAction(stack));
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return stack.getOrDefault(ModItemComponents.USE_TIME, super.getMaxUseTime(stack, user));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		stack.streamAll(SocWarsTooltipAppender.class).forEach(appender -> appender.appendTooltip(context, textConsumer, type, this.getComponents()));
		stack.streamAll(DynamicTooltipComponent.class).forEach(creator -> creator.create(stack, getWorldTime()).forEach(textConsumer));
	}

	@Override
	public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return oldStack.get(ModItemComponents.NO_COMPONENT_UPDATE_BOB) == null;
	}

	public static float getUseScale(ItemStack stack) {
		return stack.streamAll(ScaledUseDuration.class).map(component -> component.getScale(stack)).reduce(1f, (a, b) -> a * b);
	}
}
