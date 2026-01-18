package com.soc.screenhandler;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public interface ScreenHandlers {
    static void initialise() {}

    ScreenHandlerType<BedwarsIndividualShopScreenHandler> BEDWARS_SHOP_SCREEN_HANDLER = register("individual_bedwars_shop", BedwarsIndividualShopScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }
}
