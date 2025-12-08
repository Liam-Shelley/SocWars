package com.soc.game.manager.bedwars;

import com.soc.game.manager.GameEvents;

public interface BedwarsGameEvents {
    static void initialise() {
        GameEvents.registerEvent("", null);
    }
}
