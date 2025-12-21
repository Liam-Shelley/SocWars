package com.soc.game.manager;

import com.soc.lib.Events;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface PrescheduledEvents {
    static void playCountdown(Runnable endCallback, AbstractGameManager<?, ?, ?> context, int count, int interval, int startDelay, @Nullable SoundEvent sound, @Nullable ServerPlayerEntity player) {
        final Events events = Events.getInstance();

        for (int i = 0; i < count; i++) {
            final int time = i * interval + startDelay;
            final Text text = Text.of(String.valueOf(count - i));
            events.scheduleEvent(manager -> {
                if (player == null) {
                    manager.broadcastTitle(text);
                    if (sound != null) manager.broadcastSound(sound);
                } else {
                    player.networkHandler.sendPacket(new TitleS2CPacket(text));
                    if (sound != null) player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
                }
            }, context, time);
        }

        events.scheduleEvent(manager -> {
            endCallback.run();

            if (player == null) {
                manager.clearTitle();
                if (sound != null) manager.broadcastSound(sound);
            } else {
                player.networkHandler.sendPacket(new ClearTitleS2CPacket(false));
                if (sound != null) player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
            }
        }, context, (long) count * interval + startDelay);
    }

    static void playCountdown(Runnable endCallback, AbstractGameManager<?, ?, ?> context, int count, int interval, @Nullable SoundEvent sound, @Nullable ServerPlayerEntity player) {
        playCountdown(endCallback, context, count, interval, 0, sound, player);
    }

    static void playCountdown(Runnable endCallback, AbstractGameManager<?, ?, ?> context, int count, int interval, @Nullable ServerPlayerEntity player) {
        playCountdown(endCallback, context, count, interval, 0, null, player);
    }
}
