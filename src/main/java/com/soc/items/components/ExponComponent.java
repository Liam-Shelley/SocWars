package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ExponComponent(long lastTimeUsed, int damageStage) { //0 is a signal value for unset time
    public static final ExponComponent DEFAULT = new ExponComponent(0, 0);

    public static final Codec<ExponComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.LONG.fieldOf("last_time_used").forGetter(ExponComponent::lastTimeUsed),
            Codec.INT.fieldOf("damage").forGetter(ExponComponent::damageStage)
    ).apply(builder, ExponComponent::new));

    public ExponComponent doubleAndRefresh(long timeUsed) {
        return new ExponComponent(timeUsed, this.damageStage + 1);
    }
}
