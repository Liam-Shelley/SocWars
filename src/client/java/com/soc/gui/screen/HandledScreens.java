package com.soc.gui.screen;

import com.soc.screenhandler.ScreenHandlers;

import static net.minecraft.client.gui.screen.ingame.HandledScreens.register;

public class HandledScreens {
    public static void initialise() {
        register(ScreenHandlers.BEDWARS_SHOP_SCREEN_HANDLER, BedwarsShopBase::new);
    }
}
