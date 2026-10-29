package com.soc.entities;

import com.soc.entities.util.ModEntities;
import com.soc.util.DamageTypes;
import com.soc.util.SphereExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;

public class HolyHandGrenadeEntity extends HandGrenadeEntity {
    public HolyHandGrenadeEntity(EntityType<? extends ThrownEntity> type, World world, float detonationTime) {
        super(type, world, detonationTime);
    }

    public HolyHandGrenadeEntity(EntityType<? extends ThrownEntity> type, World world, float detonationTime, Entity owner) {
        super(type, world, detonationTime, owner);
    }

	public HolyHandGrenadeEntity(LivingEntity user) {
		this(ModEntities.HOLY_HAND_GRENADE, user.getWorld(), 0.5f, user);
	}

	@Override
    protected void detonate() {
        SphereExplosion.explode(this.getWorld(), this.getPos(), 8f, 1.5f, 7f, 1f, false, this.getOwner(), DamageTypes.HOLY_HAND_GRENADE);
    }
}
