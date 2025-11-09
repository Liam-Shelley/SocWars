package com.soc.events;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface OnItemPickup {
    void onItemPickup(ServerPlayerEntity player, ItemStack stack);
}
