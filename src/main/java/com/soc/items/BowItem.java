package com.soc.items;

import com.soc.items.util.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.soc.lib.SocWarsLib.getHoldTimeSeconds;
import static com.soc.lib.SocWarsLib.hasInfinity;

public class BowItem extends RangedWeaponItem {
    private final ArrowEntity arrow;
    private final Function<ItemStack, Float> drawTime;
    private final Function<ItemStack, Float> speed;
    private final float knockbackMultiplier;

    public BowItem(Settings settings, ArrowEntity arrow, Function<ItemStack, Float> drawTime, Function<ItemStack, Float> speed, float knockbackMultiplier) {
        super(settings);
        this.arrow = arrow;
        this.drawTime = drawTime;
        this.speed = speed;
        this.knockbackMultiplier = knockbackMultiplier;
    }

    public static void initialise() {
        ModItems.addItemToGroups(BOOM_BOW, ItemGroups.COMBAT);
    }

    public static final Item BOOM_BOW = ModItems.register("boom_bow", settings -> new BowItem(settings, null, stack -> 1f, stack -> 4f, 1f), new Settings());

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        boolean hasArrowOrInfinity = !user.getProjectileType(stack).isEmpty() || hasInfinity(stack);
        if (user.isInCreativeMode() || hasArrowOrInfinity) {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        final float drawProgress = this.drawProgress(stack, remainingUseTicks);
        final float speed = drawProgress * this.speed.apply(stack);

        if (world instanceof ServerWorld serverWorld) {
            this.shootAll(serverWorld, user, user.getActiveHand(), stack, List.of(Items.ARROW.getDefaultStack()), speed, 1f - drawProgress, drawProgress > 0.95f, null);
        }

        if (user instanceof PlayerEntity player) stack.damage(1, player);

        return true;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    @Override
    public int getRange() {
        return this.speed.apply(this.getDefaultStack()).intValue() * 5;
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + yaw * 0.25f, 0f, speed, divergence);
    }

    private float drawProgress(ItemStack stack, int remainingUseTicks) {
        final float getHeldAmount = getHoldTimeSeconds(remainingUseTicks);
        final float drawTime = this.drawTime.apply(stack);

        return Math.min(1f, getHeldAmount / drawTime);
    }
}
