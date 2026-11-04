package com.soc.itemsrework.armour;

import com.soc.SocWars;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import static net.minecraft.item.equipment.ArmorMaterials.createDefenseMap;

public interface ModArmourMaterials {
	RegistryKey<EquipmentAsset> DEMOLITIONIST_MODEL_KEY = registerEquipmentAsset("demolitionist");
	ArmorMaterial DEMOLITIONIST = new ArmorMaterial(
			32, createDefenseMap(3, 6, 7, 4, 10), 15, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.0F, 0.0F, null, DEMOLITIONIST_MODEL_KEY
	);
	RegistryKey<EquipmentAsset> STEADFAST_MODEL_KEY = registerEquipmentAsset("steadfast");
	ArmorMaterial STEADFAST = new ArmorMaterial(
			24, createDefenseMap(3, 5, 7, 3, 3), 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, null, STEADFAST_MODEL_KEY
	);
	RegistryKey<EquipmentAsset> GAMBLER_MODEL_KEY = registerEquipmentAsset("steadfast");
	ArmorMaterial GAMBLER = new ArmorMaterial(
			24, createDefenseMap(0, 0, 0, 0, 0), 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, null, GAMBLER_MODEL_KEY
	);

	static RegistryKey<EquipmentAsset> registerEquipmentAsset(String name) {
		return RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of(SocWars.MOD_ID, name));
	}
}
