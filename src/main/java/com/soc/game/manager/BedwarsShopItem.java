package com.soc.game.manager;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class BedwarsShopItem {
    public abstract void buy(ServerPlayerEntity player);
}
