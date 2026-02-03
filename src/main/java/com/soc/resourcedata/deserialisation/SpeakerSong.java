package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import com.soc.SocWars;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static com.soc.lib.json.JsonHelper.TIME_KEY;

public record SpeakerSong(SoundEvent soundEvent, int timeInTicks) {
    public static final String SOUND_EVENT_KEY = "sound";

    public static Optional<SpeakerSong> getSpeakerSoundFromJson(JsonObject json) {
        if (!json.has(TIME_KEY) || !json.has(SOUND_EVENT_KEY)) return returnEmpty(json);

        try {
            final Identifier id = Identifier.of(json.get(SOUND_EVENT_KEY).getAsString());
            if (!Registries.SOUND_EVENT.containsId(id)) return returnEmpty(json);

            final SoundEvent soundEvent = Registries.SOUND_EVENT.get(id);
            return Optional.of(new SpeakerSong(soundEvent, json.get(TIME_KEY).getAsInt()));

        } catch (Exception ignored) {
            return returnEmpty(json);
        }
    }

    private static Optional<SpeakerSong> returnEmpty(JsonObject json) {
        SocWars.LOGGER.warn("Could not read speaker sound and is thus ignoring from json: {}", json.toString());
        return Optional.empty();
    }
}
