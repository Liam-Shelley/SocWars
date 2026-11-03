package com.soc.itemsrework.armour;

import com.soc.SocWars;
import com.soc.itemsrework.components.ArmourSetBonus;
import com.soc.lib.ModEntityAttributes;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

public interface ArmourSetBonuses {
	ArmourSetBonus DEMOLITIONIST = new ArmourSetBonus(ModEntityAttributes.EXPLOSION_RESISTANCE, Identifier.of(SocWars.MOD_ID, "demolitionist"));
	ArmourSetBonus STEADFAST = new ArmourSetBonus(EntityAttributes.KNOCKBACK_RESISTANCE, Identifier.of(SocWars.MOD_ID, "steadfast"));
}
