package com.soc.gui.screen;

import com.soc.screenhandler.ScreenHandlers;

import static net.minecraft.client.gui.screen.ingame.HandledScreens.register;

public class HandledScreens {
    public static void initialise() {
        register(ScreenHandlers.BEDWARS_INDIVIDUAL_SHOP_SCREEN_HANDLER, BedwarsIndividualShopScreen::new);
        register(ScreenHandlers.BEDWARS_TEAM_SHOP_SCREEN_HANDLER, BedwarsTeamShopScreen::new);
    }
}
