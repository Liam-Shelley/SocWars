package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

import static com.soc.lib.SocWarsLib.hasInfinity;
import static net.minecraft.item.RangedWeaponItem.BOW_PROJECTILES;
import static net.minecraft.item.RangedWeaponItem.load;

public record BowComponent<T extends ArrowEntity>(float drawTime, float arrowSpeed, EntityType<T> arrowType) implements ItemStopUsingComponent, ItemUseComponent, ScaledUseDuration {
	public static final Codec<BowComponent<?>> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.FLOAT.fieldOf("draw_time").orElse(1f).forGetter(BowComponent::drawTime),
			Codec.FLOAT.fieldOf("arrow_speed").orElse(1f).forGetter(BowComponent::arrowSpeed),
			EntityType.CODEC.fieldOf("entity_type").forGetter(BowComponent::arrowType)
	).apply(builder, BowComponent::fromCodec));

	@SuppressWarnings("unchecked")
	private static <T extends ArrowEntity> BowComponent<?> fromCodec(float drawTime, float velocity, EntityType<?> arrowType) {
		return new BowComponent<>(drawTime, velocity, (EntityType<T>)arrowType);
	}

	public static void playBowSound(World world, LivingEntity user, float drawAmount) {
		world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.ENTITY_ARROW_SHOOT,
				SoundCategory.PLAYERS,
				1f,
				1f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + drawAmount * 0.5f
		);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (!getProjectileType(user.getStackInHand(hand), user, BOW_PROJECTILES).isEmpty()) {
			user.setCurrentHand(hand);
			return ActionResult.CONSUME;
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		final float drawProgress = this.drawProgress(stack, remainingUseTicks, user);
		if (!this.drawnEnoughToShoot(stack, remainingUseTicks, user, drawProgress)) return false;

		final List<ItemStack> arrowStack = loadWithInfinity(stack, user);

		if (world instanceof ServerWorld serverWorld) {
			this.shootAll(serverWorld, user, user.getActiveHand(), stack, arrowStack, this.arrowSpeed * drawProgress, (1f - drawProgress) * 3f, drawProgress > 0.95f);
		}

		if (user instanceof PlayerEntity player) {
			stack.damage(arrowStack.size(), player);
		}

		playBowSound(world, user, drawProgress);

		return false;
	}

	private void shootAll(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack stack, List<ItemStack> projectiles, float speed, float divergence, boolean critical) {
		final float spread = EnchantmentHelper.getProjectileSpread(world, stack, shooter, 0f);

		for(int i = 0; i < projectiles.size(); i++) {
			final ItemStack projectileStack = projectiles.get(i);
			final float yaw = shooter.getYaw() + spread * (projectiles.size() - 1 - 2 * i);

			final ProjectileEntity arrowEntity = this.createArrowEntity(world, shooter, stack, projectileStack, critical);
			if (arrowEntity != null) ProjectileEntity.spawn(arrowEntity, world, projectileStack, projectile -> projectile.setVelocity(shooter, shooter.getPitch(), yaw, 0f, speed, divergence));
		}
		stack.damage(projectiles.size(), shooter, LivingEntity.getSlotForHand(hand));
	}

	private ProjectileEntity createArrowEntity(ServerWorld world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
		final ArrowEntity arrowEntity = this.arrowType.create(world, SpawnReason.SPAWN_ITEM_USE);
		if (arrowEntity == null) return null;

		final Vec3d eyePos = shooter.getEyePos();
		arrowEntity.setPosition(eyePos.x, eyePos.y - 0.1d, eyePos.z);
		arrowEntity.setOwner(shooter);

		arrowEntity.stack = projectileStack.copyWithCount(1);
		arrowEntity.copyComponentsFrom(projectileStack);

		if (weaponStack != null && !weaponStack.isEmpty()) {
			arrowEntity.weapon = weaponStack.copy();
			final int piercingLevel = EnchantmentHelper.getProjectilePiercing(world, weaponStack, projectileStack);
			if (piercingLevel > 0) {
				arrowEntity.setPierceLevel((byte) piercingLevel);
			}
		}

		if (projectileStack.get(DataComponentTypes.INTANGIBLE_PROJECTILE) != null) {
			arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
		}

		arrowEntity.setCritical(critical);

		return arrowEntity;
	}

	public static List<ItemStack> loadWithInfinity(ItemStack stack, LivingEntity shooter) {
		final List<ItemStack> load = load(stack, getProjectileType(stack, shooter, BOW_PROJECTILES), shooter);
		if (load.isEmpty() && hasInfinity(stack)) return List.of(new ItemStack(Items.ARROW));
		return load;
	}

	public static ItemStack getProjectileType(ItemStack weaponStack, LivingEntity shooter, Predicate<ItemStack> projectilePredicate) {
		final ItemStack projectileStack = RangedWeaponItem.getHeldProjectile(shooter, projectilePredicate);

		if (!(shooter instanceof PlayerEntity playerShooter) || !projectileStack.isEmpty()) {
			return projectileStack.isEmpty() ? new ItemStack(Items.ARROW) : projectileStack;
		} else {
			for (ItemStack itemStack : playerShooter.getInventory()) {
				if (projectilePredicate.test(itemStack)) {
					return itemStack;
				}
			}

			return shooter.isInCreativeMode() || hasInfinity(weaponStack) ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
		}
	}

	//region bunch of drawProgress stuff I don't want to look at
	private float rawDrawTime(ItemStack stack, int remainingUseTicks, LivingEntity user) {
		return (stack.getMaxUseTime(user) - remainingUseTicks) / 20f;
	}

	private float drawProgress(ItemStack stack, int remainingUseTicks, LivingEntity user) {
		final float getHeldAmount = this.rawDrawTime(stack, remainingUseTicks, user);
		return Math.min(1f, getHeldAmount / this.drawTime);
	}

	private boolean drawnEnoughToShoot(ItemStack stack, int remainingUseTicks, LivingEntity user, float drawProgress) {
		return drawProgress > 0.3f || this.rawDrawTime(stack, remainingUseTicks, user) > 0.2f;
	}
	//endregion

	@Override
	public float getScale(ItemStack stack) {
		return this.drawTime;
	}
}
