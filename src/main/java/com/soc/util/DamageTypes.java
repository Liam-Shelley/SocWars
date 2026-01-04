package com.soc.util;

import com.soc.SocWars;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface DamageTypes {
    RegistryKey<DamageType> LIFETHIEF = register("lifethief");
    RegistryKey<DamageType> NETHERWRONG_SWORD = register("netherwrong_sword");
    RegistryKey<DamageType> GALLIUM = register("gallium");
    RegistryKey<DamageType> CATASTROPHE_BOW = register("catastrophe_bow");
    RegistryKey<DamageType> EXPON = register("expon");
    RegistryKey<DamageType> FULL_METAL_SWORD = register("full_metal_sword");
    RegistryKey<DamageType> SPHERE_EXPLOSION = register("sphere_explosion");

    static RegistryKey<DamageType> register(String id) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SocWars.MOD_ID, id));
    }
}

