package com.soc.items;

import com.soc.resourcedata.deserialisation.SpeakerSong;
import com.soc.resourcedata.listeners.SpeakerSongData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SpeakerItem extends Item {
    //Canned until I find some workaround for the fact that you can't have movable sound sources
    private final List<SpeakerSong> playlist;

    private LinkedList<SpeakerSong> queue = new LinkedList<>();
    private int ticksRemaining;

    public SpeakerItem(Settings settings, Identifier playlistId) {
        super(settings);
        this.playlist = SpeakerSongData.INSTANCE.getPlaylist(playlistId);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (this.ticksRemaining-- < 0) {
            this.startSong(entity);
        }
    }

    private void startSong(Entity entity) {
        if (this.queue.isEmpty()) {
            this.queue.addAll(this.playlist);
            Collections.shuffle(this.queue);
        }
        final SpeakerSong song = this.queue.remove();
        this.ticksRemaining = song.timeInTicks();
    }
}
