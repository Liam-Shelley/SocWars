package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.networking.s2c.AddVelocityPayload;
import com.soc.util.DamageTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import static com.soc.itemsrework.components.ModItemComponentsReworked.EXPON;
import static com.soc.lib.SocWarsLib.damageSource;
import static com.soc.lib.SocWarsLib.ifNotNull;

public record ExponComponent(long lastTimeUsed, int damageStage) implements ItemHitComponent, ItemTickComponent { //0 is a signal value for unset time
	//public static final int MAX_DAMAGE_STAGE = 8; //Final damage is (1 << stage) * 0.5f; this caps it to 32 damage

	public static final ExponComponent DEFAULT = new ExponComponent();

	public static final Codec<ExponComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.LONG.fieldOf("last_time_used").forGetter(ExponComponent::lastTimeUsed),
			Codec.INT.fieldOf("damage").forGetter(ExponComponent::damageStage)
	).apply(builder, ExponComponent::new));

	public ExponComponent() {
		this(0L, 0);
	}

	public int getDamage() {
		return 1 << this.damageStage;
	}

	public ExponComponent doubleAndRefresh(long timeUsed) {
		return new ExponComponent(timeUsed, this.damageStage + 1);
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		ifNotNull(stack.get(EXPON), component -> {
			final ServerWorld world = (ServerWorld) target.getWorld();
			final float damage = (1L << component.damageStage());
			if (attacker instanceof PlayerEntity playerEntity) {
				playerEntity.sendMessage(Text.of(String.valueOf(damage)), true); //maybe make a hud in 1.1?
			}

			target.damage(world, damageSource(world, DamageTypes.EXPON, attacker), damage);
			stack.set(EXPON, component.doubleAndRefresh(world.getTime()));
			stack.damage(component.damageStage() - 1, attacker, Hand.MAIN_HAND);

			final Vec3d knockback = attacker.getRotationVector().multiply(0.2d).add(0d, 0.15d, 0d);
			if (target instanceof ServerPlayerEntity player) {
				ServerPlayNetworking.send(player, new AddVelocityPayload(knockback));
			} else {
				target.addVelocity(knockback);
			}
		});
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
		ifNotNull(stack.get(ModItemComponentsReworked.EXPON), component -> {
			final long lastTimeUsed = component.lastTimeUsed();
			if (lastTimeUsed == 0) return;

			final boolean shouldReset = world.getTime() - lastTimeUsed > 4 * 20;
			if (shouldReset) stack.set(ModItemComponentsReworked.EXPON, ExponComponent.DEFAULT);
		});
	}
}
