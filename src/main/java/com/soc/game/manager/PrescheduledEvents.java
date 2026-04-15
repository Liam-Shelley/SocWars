package com.soc.game.manager;

import com.soc.lib.Events;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public interface PrescheduledEvents {
    static void playCountdown(Runnable endCallback, AbstractGameManager<?, ?, ?> context, int count, int interval, int startDelay, @Nullable SoundEvent sound, boolean isTitle, ServerPlayerEntity... players) {
        final Events events = Events.getInstance();

        for (int i = 0; i < count; i++) {
            final int time = i * interval + startDelay;
            final Text text = Text.literal(String.valueOf(count - i)).formatted(switch (count - i) {
                case 1 -> Formatting.RED;
                case 2 -> Formatting.YELLOW;
                default -> Formatting.DARK_GREEN;
            });
            events.scheduleEvent(manager -> {
                if (players.length == 0) {
                    manager.broadcastTitle(text, isTitle);
                    if (sound != null) manager.broadcastSound(sound);
                } else {
                    for (ServerPlayerEntity player : players) {
                        player.networkHandler.sendPacket(isTitle ? new TitleS2CPacket(text) : new SubtitleS2CPacket(text));
                        if (sound != null) player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
                    }
                }
            }, context, time);
        }

        events.scheduleEvent(manager -> {
            endCallback.run();

            if (players.length == 0) {
                manager.clearTitle();
                if (sound != null) manager.broadcastSound(sound);
            } else {
                for (ServerPlayerEntity player : players) {
                    player.networkHandler.sendPacket(new ClearTitleS2CPacket(true));
                    if (sound != null) player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
                }
            }
        }, context, (long)count * interval + startDelay);
    }

    static void playCountdown(Runnable endCallback, AbstractGameManager<?, ?, ?> context, int count, int interval, @Nullable SoundEvent sound, boolean isTitle, ServerPlayerEntity... players) {
        playCountdown(endCallback, context, count, interval, 0, sound, isTitle, players);
    }
}
