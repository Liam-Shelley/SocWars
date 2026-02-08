package com.soc.lib;

import com.soc.SocWars;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public interface EntityAttributes {
    static void initialise() {}

    RegistryEntry<EntityAttribute> EXPLOSION_RESISTANCE = register(
            "explosion_resistance", new ClampedEntityAttribute("attribute.name.explosion_resistance", 0.0, 0.0, 1.0).setCategory(EntityAttribute.Category.POSITIVE)
    );

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(SocWars.MOD_ID, id), attribute);
    }
}
