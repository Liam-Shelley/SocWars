package com.soc.resourcedata;

import com.soc.SocWars;
import com.soc.resourcedata.listeners.BedwarsGeneratorData;
import com.soc.resourcedata.listeners.BedwarsShopData;
import com.soc.resourcedata.listeners.SkywarsLootData;
import com.soc.resourcedata.listeners.SpeakerSongData;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ResourceManager {
    public static final Predicate<Identifier> BASE_PATH_PREDICATE = path -> path.toString().endsWith(".json");
    public static Predicate<Identifier> endsWithStringPredicate(String... endsWith) {
        return path -> {
            for (String ending : endsWith) {
                if (path.toString().endsWith(ending)) return true;
            }
            return false;
        };
    }

    public static void initialise() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SkywarsLootData.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BedwarsGeneratorData.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BedwarsShopData.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SpeakerSongData.INSTANCE);
    }

    public static void readResources(net.minecraft.resource.ResourceManager manager, String path, Predicate<Identifier> pathPredicate, BiConsumer<BufferedReader, Identifier> resourceCallback) {
        for(Identifier id : manager.findResources(path, pathPredicate).keySet()) {
            try(BufferedReader reader = manager.getResource(id).get().getReader()) {
                resourceCallback.accept(reader, id);
            } catch(Exception e) {
                SocWars.LOGGER.error("Error occurred while loading resource json {}:\n{}", id.toString(), e);
            }
        }
    }
}
