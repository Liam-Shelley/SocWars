package com.soc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ModEvents {
    static void initialise() {}

    Event<OnDamageTaken> ON_PLAYER_DAMAGE_TAKEN = EventFactory.createArrayBacked(OnDamageTaken.class, listeners -> (target, source, amount) -> {
        boolean allowEvent = true;

        for (OnDamageTaken listener : listeners) {
            allowEvent &= listener.onDamage(target, source, amount);
        }

        return allowEvent;
    });
}
