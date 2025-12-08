package com.soc.game.manager;

import com.soc.game.manager.bedwars.BedwarsGameEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface GameEvents {
    static void initialise() {
        BedwarsGameEvents.initialise();
    }

    Map<String, Consumer<? extends AbstractGameManager>> EVENT_REGISTRY = new HashMap<>();

    static <T extends AbstractGameManager> void registerEvent(String id, Consumer<T> function) {
        EVENT_REGISTRY.put(id, function);
    }
}
