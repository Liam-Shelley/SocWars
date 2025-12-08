package com.soc.resourcedata;

import com.soc.resourcedata.listeners.GameData;
import com.soc.resourcedata.listeners.ItemData;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class ResourceManager {
    public static void initialise() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ItemData.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(GameData.INSTANCE);
    }
}
