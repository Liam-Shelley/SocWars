package com.soc.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(GameOptions.class)
public interface GetOptionsVolumes {
	@Accessor
	Map<SoundCategory, SimpleOption<Double>> getSoundVolumeLevels();
}