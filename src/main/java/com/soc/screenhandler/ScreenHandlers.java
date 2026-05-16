package com.soc.screenhandler;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public interface ScreenHandlers {
    static void initialise() {}

    ScreenHandlerType<BedwarsIndividualShopScreenHandler> BEDWARS_INDIVIDUAL_SHOP_SCREEN_HANDLER = register("individual_bedwars_shop", BedwarsIndividualShopScreenHandler::new);
    ScreenHandlerType<BedwarsTeamShopScreenHandler> BEDWARS_TEAM_SHOP_SCREEN_HANDLER = register("team_bedwars_shop", BedwarsTeamShopScreenHandler::new);
    ScreenHandlerType<KitScreenHandler> KIT_SCREEN_HANDLER = register("kit", KitScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }
}
