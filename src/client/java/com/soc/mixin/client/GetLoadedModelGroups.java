package com.soc.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.component.ComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BakedModelManager.class)
public interface GetLoadedModelGroups {
	@Accessor
	Object2IntMap<BlockState> getModelGroups();
}