package com.soc.resourcedata.containers;

import com.soc.resourcedata.deserialisation.SpeakerSong;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeakerSongDataContainer implements CachedData {
    private final Map<Identifier, List<SpeakerSong>> playlistIdMap = new HashMap<>();

    @Override
    public void cache() {

    }

    @Override
    public void clear() {
        this.playlistIdMap.clear();
    }

    public List<SpeakerSong> getPlaylist(Identifier id) {
        return this.playlistIdMap.get(id);
    }

    public void addSongToPlaylist(Identifier id, SpeakerSong speakerSong) {
        this.playlistIdMap.computeIfAbsent(id, a -> new ArrayList<>()).add(speakerSong);
    }
}
