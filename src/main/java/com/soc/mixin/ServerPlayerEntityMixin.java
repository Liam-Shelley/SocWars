package com.soc.mixin;

import com.soc.items.components.ModItemComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

import static com.soc.lib.SocWarsLib.ARMOUR_SLOTS;
import static com.soc.lib.SocWarsLib.damageSource;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	private void socwars$tickCactusArmour(CallbackInfo ci) {
		if (this.isSpectator() || this.getWorld().getTime() % 10 > 0) return;

		final int pieces = (int) Arrays.stream(ARMOUR_SLOTS).filter(slot -> this.getEquippedStack(slot).contains(ModItemComponents.CACTUS_ARMOUR)).count();

		if (pieces > 0) {
			final Box box = new Box(this.getPos().x - 1.5d, this.getPos().y, this.getPos().z - 1.5d, this.getPos().x + 1.5d, this.getPos().y + 1d, this.getPos().z + 1.5d);
			final List<Entity> otherEntities = this.getWorld().getOtherEntities((Entity)(Object)this, box);

			if (!otherEntities.isEmpty()) {
				final DamageSource source = damageSource(this.getWorld(), DamageTypes.CACTUS, (Entity)(Object)this);
				for (Entity otherEntity : otherEntities) {
					otherEntity.damage((ServerWorld)this.getWorld(), source, pieces * 1.5f);
				}
			}
		}
	}
}
