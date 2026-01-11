package com.soc.game.manager.bedwars;

import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;

public enum ShopType {
    INDIVIDUAL(BedwarsShopScreenHandler::new),
    TEAM(null);

    private final ScreenHandlerFactory factory;

    ShopType(ScreenHandlerFactory factory) {
        this.factory = factory;
    }
}
