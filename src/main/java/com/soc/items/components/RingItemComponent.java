package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RingItemComponent(long lastTimeUsed, boolean isUsing) {
    public static final Codec<RingItemComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.LONG.fieldOf("last_time_used").forGetter(RingItemComponent::lastTimeUsed),
            Codec.BOOL.optionalFieldOf("is_using", false).forGetter(RingItemComponent::isUsing)
    ).apply(builder, RingItemComponent::new));

    public RingItemComponent withLastTimeUsed(long lastTimeUsed) {
        return new RingItemComponent(lastTimeUsed, this.isUsing);
    }

    public RingItemComponent withIsUsing(boolean isUsing) {
        return new RingItemComponent(this.lastTimeUsed, isUsing);
    }
}
