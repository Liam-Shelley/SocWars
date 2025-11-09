package com.soc.resourcedata;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class ResourceManager {
    public static final Gson GSON = new Gson();

    public static final ItemData ITEM_DATA = new ItemData();
    public static final GameData GAME_DATA = new GameData();

    public static void initialise() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ITEM_DATA);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(GAME_DATA);
    }
}
