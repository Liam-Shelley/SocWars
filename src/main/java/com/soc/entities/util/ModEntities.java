package com.soc.entities.util;

import com.soc.SocWars;
import com.soc.entities.*;
import com.soc.game.manager.bedwars.ShopType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface ModEntities {
    static void initialise() {}

    EntityType<BWFireballEntity> BW_FIREBALL = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "bw_fireball"), EntityType.Builder.<BWFireballEntity>create(BWFireballEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(1)
    );
    EntityType<BigTntEntity> NUCLEAR_BOMB = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "nuclear_bomb"), EntityType.Builder.<BigTntEntity>create((type, world) -> new BigTntEntity(type, world, BigTntEntity.BigTntType.NUCLEAR), SpawnGroup.MISC)
            .dropsNothing()
            .makeFireImmune()
            .dimensions(0.98f, 0.98f)
            .eyeHeight(0.15f)
            .maxTrackingRange(10)
            .trackingTickInterval(10)
    );
    EntityType<BigTntEntity> HYDROGEN_BOMB = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "hydrogen_bomb"), EntityType.Builder.<BigTntEntity>create((type, world) -> new BigTntEntity(type, world, BigTntEntity.BigTntType.HYDROGEN), SpawnGroup.MISC)
            .dropsNothing()
            .makeFireImmune()
            .dimensions(0.98f, 0.98f)
            .eyeHeight(0.15f)
            .maxTrackingRange(10)
            .trackingTickInterval(10)
    );
    EntityType<EnderBeamEntity> ENDER_BEAM = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "ender_beam"), EntityType.Builder.create(EnderBeamEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.25f, 0.25f)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
    );
    EntityType<BedwarsShopEntity> INDIVIDUAL_BEDWARS_SHOP = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "individual_bedwars_shop"), EntityType.Builder.<BedwarsShopEntity>create((type, world) -> new BedwarsShopEntity(type, world, ShopType.INDIVIDUAL), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.6f, 1.95f)
            .maxTrackingRange(4)
            .eyeHeight(1.62f)
    );
    EntityType<BedwarsShopEntity> TEAM_BEDWARS_SHOP = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "team_bedwars_shop"), EntityType.Builder.<BedwarsShopEntity>create((type, world) -> new BedwarsShopEntity(type, world, ShopType.TEAM), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.6f, 1.95f)
            .maxTrackingRange(4)
            .eyeHeight(1.62f)
    );
    EntityType<HandGrenadeEntity> HAND_GRENADE = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "hand_grenade"), EntityType.Builder.<HandGrenadeEntity>create((type, world) -> new HandGrenadeEntity(type, world, 0.5f), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.5f, 0.625f)
            .maxTrackingRange(4)
            .eyeHeight(0.6f)
    );
    EntityType<HolyHandGrenadeEntity> HOLY_HAND_GRENADE = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "holy_hand_grenade"), EntityType.Builder.<HolyHandGrenadeEntity>create((type, world) -> new HolyHandGrenadeEntity(type, world, 0.5f), SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.5f, 0.85f)
            .maxTrackingRange(4)
            .eyeHeight(0.6f)
    );
    EntityType<MolotovCocktailEntity> MOLOTOV_COCKTAIL = ModEntities.registerType(Identifier.of(SocWars.MOD_ID, "molotov_cocktail"), EntityType.Builder.<MolotovCocktailEntity>create(MolotovCocktailEntity::new, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0.2f, 0.5f)
            .maxTrackingRange(4)
            .eyeHeight(0.4f)
    );

    static <T extends Entity> EntityType<T> registerType(Identifier id, EntityType.Builder<T> type) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }
}
