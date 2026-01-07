package com.soc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

import java.util.Arrays;

public interface ModEvents {
    static void initialise() {}

    Event<OnDamageTaken> ON_PLAYER_DAMAGE_TAKEN = EventFactory.createArrayBacked(OnDamageTaken.class, listeners -> (target, source, amount) -> {
        boolean allowEvent = true;

        for (OnDamageTaken listener : listeners) {
            allowEvent &= listener.onDamage(target, source, amount);
        }

        return allowEvent;
    });

    Event<OnChestOpened> ON_CHEST_OPENED = EventFactory.createArrayBacked(OnChestOpened.class, listeners -> (player, chestPos) -> {
        for (OnChestOpened listener : listeners) {
            listener.onChestOpen(player, chestPos);
        }
    });

    Event<OnItemPickup> ON_ITEM_PICKUP = EventFactory.createArrayBacked(OnItemPickup.class, listeners -> (player, itemStack) -> {
        for (OnItemPickup listener : listeners) {
            listener.onItemPickup(player, itemStack);
        }
    });

    Event<OnBlockPlaced> ON_BLOCK_PLACED = EventFactory.createArrayBacked(OnBlockPlaced.class, listeners -> (player, pos, context) -> {
        for (OnBlockPlaced listener : listeners) {
            final ActionResult result = listener.onItemPickup(player, pos, context);
            if (result != ActionResult.PASS) return result;
        }

        return ActionResult.PASS;
    });
}
