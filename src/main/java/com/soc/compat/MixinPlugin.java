package com.soc.compat;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MixinPlugin implements IMixinConfigPlugin {
	public static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();

	public static final Map<String, List<String>> MIXIN_COMPAT_MAP = new HashMap<>(Map.of(
			"JadeBlockMorphSpoof", List.of("jade"),
			"DisallowFreecam", List.of("freecam"),
			"RenderPlayersThroughMorphInFreecam", List.of("freecam")
	));

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		final String[] mixinClassNameSegments = mixinClassName.split("\\.");
		final String className = mixinClassNameSegments[mixinClassNameSegments.length - 1];

		if (MIXIN_COMPAT_MAP.containsKey(className)) {
			return MIXIN_COMPAT_MAP.get(className).stream().map(FABRIC_LOADER::isModLoaded).reduce(true, Boolean::logicalAnd);
		}

		return true;
	}
}
