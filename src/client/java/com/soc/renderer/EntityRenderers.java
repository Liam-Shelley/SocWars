package com.soc.renderer;

import com.soc.SocWars;
import com.soc.entities.util.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;

public interface EntityRenderers {
    static void initialise() {
        EntityRendererRegistry.register(ModEntities.NUCLEAR_BOMB, BigTntRenderer::new);
        EntityRendererRegistry.register(ModEntities.HYDROGEN_BOMB, BigTntRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIREBALL, context -> new SimpleBillboardEntityRenderer(context, Identifier.ofVanilla("textures/item/fire_charge.png")));
        EntityRendererRegistry.register(ModEntities.WATERBALL, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/waterball.png")));
        EntityRendererRegistry.register(ModEntities.SNAIL_FIREBALL, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/snail_fireball.png")));
        EntityRendererRegistry.register(ModEntities.LIGHTNING_ORB, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/lightning_orb.png")));
        EntityRendererRegistry.register(ModEntities.INDIVIDUAL_BEDWARS_SHOP, BedwarsShopEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.TEAM_BEDWARS_SHOP, BedwarsShopEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HAND_GRENADE, HandGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOLY_HAND_GRENADE, HolyHandGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOLOTOV_COCKTAIL, MolotovCocktailEntityRenderer::new);
    }
}
