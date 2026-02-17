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
    RegistryKey<DamageType> CATASTROPHE_BOW_BACKFIRE = register("catastrophe_bow_backfire");
    RegistryKey<DamageType> EXPON = register("expon");
    RegistryKey<DamageType> FULL_METAL_SWORD = register("full_metal_sword");
    RegistryKey<DamageType> SPHERE_EXPLOSION = register("sphere_explosion");
    RegistryKey<DamageType> HOLY_HAND_GRENADE = register("holy_hand_grenade");
    RegistryKey<DamageType> GLASS_ARMOUR = register("glass_armour");
    RegistryKey<DamageType> SNIPER_RIFLE = register("sniper_rifle");

    static RegistryKey<DamageType> register(String id) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SocWars.MOD_ID, id));
    }
}

