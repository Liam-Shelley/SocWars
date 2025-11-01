package com.soc.blocks.util;

import com.soc.SocWars;
import com.soc.blocks.*;
import com.soc.entities.BigTntEntity;
import com.soc.items.FeatherBlockItem;
import com.soc.items.util.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModBlocks {
    public static void initialise() {}

    public static final Block PLASTIC_BLOCK = ModBlocks.register("plastic_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(2f, 2), true);
    public static final Block RUBBER_BLOCK = ModBlocks.register("rubber_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(4f, 3), true);
    public static final Block PERSPEX_BLOCK = ModBlocks.register("perspex_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(2f, 1200f), true);
    public static final Block HARDENED_LAVA_BLOCK = ModBlocks.register("hardened_lava_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(30f, 1200f), settings -> settings.rarity(Rarity.RARE));
    public static final Block UNOBTANIUM_BLOCK = ModBlocks.register("unobtanium_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(60f, 1200f), settings -> settings.rarity(Rarity.RARE));
    public static final Block NEAR_INFINITE_DENSITY_BLOCK = ModBlocks.register("near_infinite_density_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(100f, 1200f), settings -> settings.rarity(Rarity.EPIC));
    public static final Block LIAM_BLOCK = ModBlocks.register("liam_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(200f, 1200f), settings -> settings.rarity(Rarity.EPIC));
    public static final Block MANY_BEDS_BLOCK = ModBlocks.register("many_beds_block", Block::new, AbstractBlock.Settings.create().requiresTool().strength(2f, 1200f), settings -> settings.rarity(Rarity.UNCOMMON));
    public static final Block FEATHER_BLOCK = ModBlocks.register("feather_block", Block::new, AbstractBlock.Settings.create().breakInstantly(), FeatherBlockItem::new);
    public static final Block GALLIUM_BLOCK = ModBlocks.register("gallium_block", settings -> new GalliumBlock(new ColorCode(0), settings), AbstractBlock.Settings.create().requiresTool().strength(0.35f, 2.5f).noCollision(), settings -> settings.rarity(Rarity.UNCOMMON));

    public static final Block SPAWN_PLACEHOLDER = ModBlocks.register("spawn_placeholder", ColourStateBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);
    public static final Block CENTRE_PLACEHOLDER = ModBlocks.register("centre_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);

    public static final Block CHEST_PLACEHOLDER = ModBlocks.register("chest_placeholder", settings -> new TierBlock(settings, 1), AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).noCollision().nonOpaque(), false);

    public static final Block DIAMOND_GEN_PLACEHOLDER = ModBlocks.register("diamond_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);
    public static final Block EMERALD_GEN_PLACEHOLDER = ModBlocks.register("emerald_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);
    public static final Block ISLAND_GEN_PLACEHOLDER = ModBlocks.register("island_gen_placeholder", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).noCollision().nonOpaque(), true);

    public static final Block MOSHPIT_MAP = ModBlocks.register("moshpit_map", Block::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOL).noCollision().nonOpaque(), true); //Maybe fix voxel shape

    public static final Block NUCLEAR_BOMB = ModBlocks.register("nuclear_bomb", (settings) -> new BigTntBlock(settings, BigTntEntity.BigTntType.NUCLEAR), AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.GRASS), true);
    public static final Block HYDROGEN_BOMB = ModBlocks.register("hydrogen_bomb", (settings) -> new BigTntBlock(settings, BigTntEntity.BigTntType.HYDROGEN), AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.GRASS), true);
    public static final Block COLLECTIBLE_BLOCK = ModBlocks.register("collectible_block", CollectibleBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON).noCollision().nonOpaque(), true); //No collision?
    public static final Block ITSEVOCAT_SKULL = ModBlocks.register("itsevocat_skull", ItsevocatSkull::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.BAMBOO).nonOpaque(), true); //No collision?
    public static final Block MAP_BLOCK = ModBlocks.register("map_block", MapBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON), true);
    public static final Block JOIN_QUEUE_BLOCK = ModBlocks.register("join_queue_block", JoinQueueBlock::new, AbstractBlock.Settings.create().sounds(BlockSoundGroup.LODESTONE).hardness(1000000f).resistance(1000000f).nonOpaque().pistonBehavior(PistonBehavior.BLOCK), true);

    /*
    public static final Block SOC_HEAD = ModBlocks.register(
            "soc_head",
            settings -> new PlayerSkullBlock(settings),
            AbstractBlock.Settings.create().instrument(NoteBlockInstrument.ZOMBIE).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY),
            true
    );
    public static final Block ITSEVOCAT_HEAD = ModBlocks.register(
            "itsevocat_head",
            settings -> new SkullBlock(SkullBlock.Type.ZOMBIE, settings),
            AbstractBlock.Settings.create().instrument(NoteBlockInstrument.ZOMBIE).strength(1.0F).pistonBehavior(PistonBehavior.DESTROY),
            true
    );
     */

    //public static final Block RED_BEDWARS_BED = ModBlocks.register("red_bedwars_bed", (settings) -> new BedwarsBed(DyeColor.RED, settings), bedSettings(DyeColor.RED), true);
    //public static final Block YELLOW_BEDWARS_BED = ModBlocks.register("yellow_bedwars_bed", (settings) -> new BedwarsBed(DyeColor.YELLOW, settings), bedSettings(DyeColor.YELLOW), true);
    //public static final Block LIME_BEDWARS_BED = ModBlocks.register("lime_bedwars_bed", (settings) -> new BedwarsBed(DyeColor.LIME, settings), bedSettings(DyeColor.LIME), true);
    //public static final Block BLUE_BEDWARS_BED = ModBlocks.register("blue_bedwars_bed", (settings) -> new BedwarsBed(DyeColor.BLUE, settings), bedSettings(DyeColor.BLUE), true);

    public static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        return ModBlocks.register(name, blockFactory, settings, shouldRegisterItem ? UnaryOperator.identity() : null);
    }

    public static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, @Nullable UnaryOperator<Item.Settings> itemSettings) {
        return ModBlocks.register(name, blockFactory, settings, itemSettings == null ? null : (block, itemKey) -> new BlockItem(block, itemSettings.apply(new Item.Settings().registryKey(itemKey))));
    }

    public static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, @Nullable BiFunction<Block, RegistryKey<Item>, ? extends BlockItem> itemFunction) {
        // Create a registry key for the block
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (itemFunction != null) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = itemFunction.apply(block, itemKey);
            Registry.register(Registries.ITEM, itemKey, blockItem);
            ModItems.addItemToGroups(blockItem.asItem(), ModItems.SOCWARS_ITEM_GROUP_KEY);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SocWars.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SocWars.MOD_ID, name));
    }

    private static AbstractBlock.Settings bedSettings(DyeColor color) {
        return AbstractBlock.Settings.create()
                .mapColor(color.getMapColor())
                .sounds(BlockSoundGroup.WOOD)
                .hardness(0.2f)
                .resistance(1000000f)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.BLOCK);
    }
}
