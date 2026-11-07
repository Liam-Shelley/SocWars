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
			24, createDefenseMap(3, 5, 7, 3, 10), 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, null, STEADFAST_MODEL_KEY
	);

	RegistryKey<EquipmentAsset> GAMBLER_MODEL_KEY = registerEquipmentAsset("gambler");
	ArmorMaterial GAMBLER = new ArmorMaterial(
			24, createDefenseMap(0, 0, 0, 0, 0), 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, null, GAMBLER_MODEL_KEY
	);

	RegistryKey<EquipmentAsset> GLASS_MODEL_KEY = registerEquipmentAsset("glass");
	ArmorMaterial GLASS = new ArmorMaterial(
			20, createDefenseMap(4, 6, 8, 2, 4), 15, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, null, GLASS_MODEL_KEY
	);

	RegistryKey<EquipmentAsset> ILLUSIONER_MODEL_KEY = registerEquipmentAsset("illusioner");
	ArmorMaterial ILLUSIONER = new ArmorMaterial(
			32, createDefenseMap(4, 6, 7, 5, 9), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, null, ILLUSIONER_MODEL_KEY
	);

	RegistryKey<EquipmentAsset> CARTOON_MODEL_KEY = registerEquipmentAsset("cartoon");
	ArmorMaterial CARTOON = new ArmorMaterial(
			24, createDefenseMap(4, 7, 9, 4, 12), 15, SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, 0.0F, 0.0F, null, CARTOON_MODEL_KEY
	);

	RegistryKey<EquipmentAsset> SUMMONERS_GARB_MODEL_KEY = ModArmourMaterials.registerEquipmentAsset("summoners_garb");
	ArmorMaterial SUMMONERS_GARB = new ArmorMaterial(
			20, createDefenseMap(0, 0, 6, 0, 8), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, null, SUMMONERS_GARB_MODEL_KEY
	);

	RegistryKey<EquipmentAsset> TRAINING_WEIGHTS_MODEL_KEY = ModArmourMaterials.registerEquipmentAsset("training_weights");
	ArmorMaterial TRAINING_WEIGHTS = new ArmorMaterial(
			20, createDefenseMap(2, 0, 0, 0, 6), 15, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F, 0.0F, null, TRAINING_WEIGHTS_MODEL_KEY
	);

	static RegistryKey<EquipmentAsset> registerEquipmentAsset(String name) {
		return RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of(SocWars.MOD_ID, name));
	}
}
