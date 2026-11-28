package com.soc.mixin;

import com.soc.mixinducks.OwnableLightningAccess;
import net.minecraft.entity.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LightningEntity.class)
public abstract class OwnableLightningMixin extends Entity implements Ownable, OwnableLightningAccess {
	@Unique
	LazyEntityReference<Entity> socwars$owner;

	public OwnableLightningMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	public @Nullable Entity getOwner() {
		return LazyEntityReference.resolve(this.socwars$owner, this.getWorld(), Entity.class);
	}

	@Override
	public @Nullable Entity socwars$getOwner() {
		return LazyEntityReference.resolve(this.socwars$owner, this.getWorld(), Entity.class);
	}

	@Override
	public void socwars$setOwner(Entity owner) {
		this.socwars$owner = new LazyEntityReference<>(owner);
	}
}
