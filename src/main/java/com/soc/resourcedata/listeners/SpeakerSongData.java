package com.soc.resourcedata.listeners;

import com.google.gson.JsonElement;
import com.soc.SocWars;
import com.soc.resourcedata.containers.SpeakerSongDataContainer;
import com.soc.resourcedata.deserialisation.SpeakerSong;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.List;
import java.util.Optional;

import static com.soc.resourcedata.ResourceManager.BASE_PATH_PREDICATE;
import static com.soc.resourcedata.ResourceManager.readResources;

public class SpeakerSongData implements SimpleSynchronousResourceReloadListener {
    public static final SpeakerSongData INSTANCE = new SpeakerSongData();

    public static final String ITEM_ID_KEY = "id";

    private final SpeakerSongDataContainer speakerSongData = new SpeakerSongDataContainer();
    public List<SpeakerSong> getPlaylist(Identifier id) { return this.speakerSongData.getPlaylist(id); }

    private SpeakerSongData() {}

    @Override
    public Identifier getFabricId() {
        return Identifier.of(SocWars.MOD_ID, "speaker_song_data_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        this.speakerSongData.clear();

        readResources(manager, "speaker_song_data", BASE_PATH_PREDICATE, (reader, id) -> {
            for (JsonElement jsonElement : JsonHelper.deserializeArray(reader)) {
                if (!jsonElement.isJsonObject()) return;
                final Optional<SpeakerSong> speakerSong = SpeakerSong.getSpeakerSoundFromJson(jsonElement.getAsJsonObject());
                speakerSong.ifPresent(song -> this.speakerSongData.addSongToPlaylist(id, song));
            }
        });

        this.speakerSongData.cache();
    }
}
