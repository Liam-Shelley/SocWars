package com.soc.util;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;

import java.util.UUID;

public interface Codecs {
    Codec<UUID> UUID = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);
}
