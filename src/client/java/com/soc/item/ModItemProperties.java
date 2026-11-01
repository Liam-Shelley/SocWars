package com.soc.item;

import com.soc.SocWars;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.util.Identifier;

public interface ModItemProperties {
	static void initialise() {
		BooleanProperties.ID_MAPPER.put(Identifier.of(SocWars.MOD_ID, "optional_property"), OptionalProperty.CODEC);
	}
}
