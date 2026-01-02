package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;

import java.util.List;

public class BaseTool extends Item {

    public BaseTool(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        ModItems.addItemToGroups(STONE_SHEARS, ItemGroups.TOOLS);
        ModItems.addItemToGroups(DIAMOND_SHEARS, ItemGroups.TOOLS);
        ModItems.addItemToGroups(EMERALD_PICKAXE, ItemGroups.TOOLS);
        ModItems.addItemToGroups(EMERALD_AXE, ItemGroups.TOOLS);
        ModItems.addItemToGroups(UNOBTANIUM_PICKAXE, ItemGroups.TOOLS);
        ModItems.addItemToGroups(UNOBTANIUM_AXE, ItemGroups.TOOLS);
        ModItems.addItemToGroups(UNOBTANIUM_SHEARS, ItemGroups.TOOLS);
        ModItems.addItemToGroups(BEDROCK_PICKAXE, ItemGroups.TOOLS);
        ModItems.addItemToGroups(BEDROCK_AXE, ItemGroups.TOOLS);
        ModItems.addItemToGroups(FISH_PICKAXE, ItemGroups.TOOLS);
        ModItems.addItemToGroups(FISH_AXE, ItemGroups.TOOLS);
        ModItems.addItemToGroups(FISH_SHEARS, ItemGroups.TOOLS);
    }

    public static final Item STONE_SHEARS = ModItems.register("stone_shears", BaseTool::new, new Settings().component(DataComponentTypes.TOOL, createShearsComponent(ToolMaterial.STONE)));
    public static final Item DIAMOND_SHEARS = ModItems.register("diamond_shears", BaseTool::new, new Settings().component(DataComponentTypes.TOOL, createShearsComponent(ToolMaterial.DIAMOND)));
    public static final Item EMERALD_PICKAXE = ModItems.register("emerald_pickaxe", BaseTool::new, new Settings().pickaxe(ToolMaterials.EMERALD, 1f, -2.8f));
    public static final Item EMERALD_AXE = ModItems.register("emerald_axe", BaseTool::new, new Settings().axe(ToolMaterials.EMERALD, 5f, -3f));
    public static final Item UNOBTANIUM_PICKAXE = ModItems.register("unobtanium_pickaxe", BaseTool::new, new Settings().pickaxe(ToolMaterials.UNOBTANIUM, 1f, -2.8f));
    public static final Item UNOBTANIUM_AXE = ModItems.register("unobtanium_axe", BaseTool::new, new Settings().axe(ToolMaterials.UNOBTANIUM, 5f, -3f));
    public static final Item UNOBTANIUM_SHEARS = ModItems.register("unobtanium_shears", BaseTool::new, new Settings().component(DataComponentTypes.TOOL, createShearsComponent(ToolMaterials.UNOBTANIUM)));
    public static final Item BEDROCK_PICKAXE = ModItems.register("bedrock_pickaxe", BaseTool::new, new Settings().pickaxe(ToolMaterials.BEDROCK, 1f, -2.8f));
    public static final Item BEDROCK_AXE = ModItems.register("bedrock_axe", BaseTool::new, new Settings().axe(ToolMaterials.BEDROCK, 5f, -3f));
    public static final Item FISH_PICKAXE = ModItems.register("fish_pickaxe", BaseTool::new, new Settings().pickaxe(ToolMaterials.FISH, 1f, -2.8f));
    public static final Item FISH_AXE = ModItems.register("fish_axe", BaseTool::new, new Settings().axe(ToolMaterials.FISH, 5f, -3f));
    public static final Item FISH_SHEARS = ModItems.register("fish_shears", BaseTool::new, new Settings().component(DataComponentTypes.TOOL, createShearsComponent(ToolMaterials.FISH)));

    public static ToolComponent createShearsComponent(ToolMaterial toolMaterial) {
        final RegistryEntryLookup<Block> registryEntryLookup = Registries.createEntryLookup(Registries.BLOCK);
        final float speed = toolMaterial.speed();

        return new ToolComponent(
                List.of(
                        ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getRegistryEntry()), speed),
                        ToolComponent.Rule.of(registryEntryLookup.getOrThrow(BlockTags.LEAVES), speed * 0.75f),
                        ToolComponent.Rule.of(registryEntryLookup.getOrThrow(BlockTags.WOOL), speed * 0.65f),
                        ToolComponent.Rule.of(RegistryEntryList.of(Blocks.VINE.getRegistryEntry(), Blocks.GLOW_LICHEN.getRegistryEntry()), speed * 0.25f)
                ),1, 1, true
        );
    }
}