package com.soc.datagen;

import com.soc.SocWars;
import com.soc.itemsrework.ModItemsReworked;
import com.soc.itemsrework.util.GenerateItem;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.Models;
import net.minecraft.item.Item;

import java.util.Arrays;

import static java.lang.reflect.Modifier.isStatic;

public class SocWarsModelProvider extends FabricModelProvider {
	public SocWarsModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {
		generateModelsForItemsDeclaredInClass(itemModelGenerator, ModItemsReworked.class);
	}

	private static void generateModelsForItemsDeclaredInClass(ItemModelGenerator itemModelGenerator, Class<?> itemsClass) {
		Arrays.stream(itemsClass.getDeclaredFields()).filter(field ->
				isStatic(field.getModifiers()) &&
				field.canAccess(null) &&
				field.isAnnotationPresent(GenerateItem.class) &&
				Item.class.isAssignableFrom(field.getType())
		).forEach(field -> {
			try {
				final Item item = (Item)field.get(null);
				final Model model = switch(field.getAnnotation(GenerateItem.class).value()) {
					case GENERATED -> Models.GENERATED;
					case HANDHELD -> Models.HANDHELD;
				};

				itemModelGenerator.register(item, model);
			} catch (Exception e) {
				SocWars.LOGGER.warn("Failed to generate item model for {}: {}", field.getName(), e.getMessage());
			}
		});
	}
}