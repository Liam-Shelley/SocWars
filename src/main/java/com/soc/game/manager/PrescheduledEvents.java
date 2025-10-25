package com.soc.game.manager;

import com.soc.lib.Events;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface PrescheduledEvents {
    static void playCountdown(Runnable endCallback, AbstractGameManager context, int count, int interval, int startDelay, @Nullable SoundEvent sound) {
        final Events events = Events.getInstance();

        for (int i = 0; i < count; i++) {
            final int time = i * interval + startDelay;
            final Text text = Text.of(String.valueOf(count - i));
            events.scheduleEvent(manager -> {
                manager.broadcastTitle(text);
                if (sound != null) manager.broadcastSound(sound);
            }, context, time);
        }

        events.scheduleEvent(manager -> {
            endCallback.run();
            manager.clearTitle();
            if (sound != null) manager.broadcastSound(sound);
        }, context, (long) count * interval + startDelay);
    }

    static void playCountdown(Runnable endCallback, AbstractGameManager context, int count, int interval, @Nullable SoundEvent sound) {
        playCountdown(endCallback, context, count, interval, 0, sound);
    }

    static void playCountdown(Runnable endCallback, AbstractGameManager context, int count, int interval) {
        playCountdown(endCallback, context, count, interval, 0, null);
    }
}
