package com.soc.entities;

import com.soc.util.DamageTypes;
import com.soc.util.SphereExplosion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;

public class HolyHandGrenadeEntity extends HandGrenadeEntity {
    public HolyHandGrenadeEntity(EntityType<? extends ThrownEntity> type, World world, float detonationTime) {
        super(type, world, detonationTime);
    }

    @Override
    protected void detonate() {
        SphereExplosion.explode(this.getWorld(), this.getPos(), 6f, 11f, 1f, false, this.getOwner(), DamageTypes.HOLY_HAND_GRENADE);
    }
}
