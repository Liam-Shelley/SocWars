package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.soc.SocWars;
import com.soc.lib.SocWarsLib;
import com.soc.resourcedata.ResourceManager;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public class SkywarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "swmap";
    public static final String LOOT_CHESTS_POS_KEY = "loot_chests_positions";
    public static final String LOOT_CHESTS_TIER_KEY = "loot_chests_tiers";

    private final ImmutableMap<BlockPos, Integer> lootChests;

    public SkywarsGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull BlockPos absoluteCentrePos,
            @NotNull ServerWorld world,
            @NotNull ImmutableMap<BlockPos, Integer> lootChests
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, world);
        this.lootChests = lootChests;
    }

    /// Constructor used only for saving the map to file
    public SkywarsGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull ImmutableMap<BlockPos, Integer> lootChests
    ) {
        super(structure, spawnPositions, centrePos);
        this.lootChests = lootChests;
    }

    public void placeLootChests() {
        this.lootChests.forEach((pos, tier) -> {
            final BlockPos chestPos = super.pos(pos).down();
            this.world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());

            final Inventory inventory = ChestBlock.getInventory((ChestBlock) Blocks.CHEST, this.world.getBlockState(chestPos), this.world, chestPos, true);
            try {
                if (inventory != null) this.populateInventory(inventory, tier);
            } catch (Exception ignored) {}
        });
    }

    private void populateInventory(Inventory inventory, int tier) {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.setStack(i, ResourceManager.ITEM_DATA.getSkywarsItemData().getRandomItem(0, tier - 1, this.world.random).getDefaultStack());
        }
    }

    public static Optional<SkywarsGameMap> loadRandomMap(@NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        Optional<File> file = AbstractGameMap.getRandomMap(FILE_EXTENSION, world, null);

        return file.flatMap(optional -> loadFromFile(file.get(), world, centrePos));
    }

    public static Optional<SkywarsGameMap> loadFromFile(File file, @NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        NbtCompound compound = null;
        try {
            compound = NbtIo.read(file.toPath());
        } catch (IOException e) {
            SocWars.LOGGER.error("Could not read compound at {}", file.getAbsolutePath());
        }

        if (compound == null) return Optional.empty();

        return fromNbt(compound, world, centrePos);
    }

    private static Optional<SkywarsGameMap> fromNbt(@NotNull NbtCompound compound, @NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        final StructureTemplateManager templateManager = world.getStructureTemplateManager();
        final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
        final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

        final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
        if (centrePosLong.isEmpty()) {
            SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
            return Optional.empty();
        }

        final Set<BlockPos> spawn_positions = getBlockPosSet(compound, SPAWN_POSITIONS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load spawn positions"); return Set.of(); });
        final Set<DyeColor> spawn_teams = Arrays.stream(compound.getIntArray(SPAWN_TEAMS_KEY).orElse(new int[0])).mapToObj(SocWarsLib::dyeColourFromOrdinal).collect(Collectors.toSet());

        final Set<BlockPos> chest_positions = getBlockPosSet(compound, LOOT_CHESTS_POS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load chest positions"); return Set.of(); });
        final List<Integer> chest_tiers = Arrays.stream(compound.getIntArray(LOOT_CHESTS_TIER_KEY).orElse(new int[0])).boxed().toList();

        return Optional.of(new SkywarsGameMap(
                template,
                mapFromCollections(spawn_teams, spawn_positions),
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                world,
                mapFromCollections(chest_positions, chest_tiers)
        ));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        putBlockPosCollection(compound, LOOT_CHESTS_POS_KEY, this.lootChests.keySet().stream().toList());
        compound.putIntArray(LOOT_CHESTS_TIER_KEY, this.lootChests.values().stream().mapToInt(tier -> tier).toArray());

        return compound;
    }

    @Override
    public void tick() {}
}