package com.soc.renderer;

import com.soc.entities.util.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public interface EntityRenderers {
    static void initialise() {
        EntityRendererRegistry.register(ModEntities.NUCLEAR_BOMB, BigTntRenderer::new);
        EntityRendererRegistry.register(ModEntities.HYDROGEN_BOMB, BigTntRenderer::new);
        EntityRendererRegistry.register(ModEntities.BW_FIREBALL, BWFireballEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.INDIVIDUAL_BEDWARS_SHOP, BedwarsShopEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.TEAM_BEDWARS_SHOP, BedwarsShopEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HAND_GRENADE, HandGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOLY_HAND_GRENADE, HolyHandGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOLOTOV_COCKTAIL, MolotovCocktailEntityRenderer::new);
    }
}
