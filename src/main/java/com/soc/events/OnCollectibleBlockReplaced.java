package com.soc.events;

import net.minecraft.server.world.ServerWorld;

public interface OnCollectibleBlockReplaced {
    void onCollectibleBlockReplaced(int id, ServerWorld serverWorld);
}
