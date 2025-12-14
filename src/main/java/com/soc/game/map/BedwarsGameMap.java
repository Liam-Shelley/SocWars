package com.soc.game.map;

import com.google.common.collect.*;
import com.soc.SocWars;
import com.soc.nbt.SpawnPosition;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public class BedwarsGameMap extends AbstractGameMap {
    public static class IslandTeam {
        private boolean hasBed = true;
        private final DyeColor colour;

        public IslandTeam(DyeColor colour) {
            this.colour = colour;
        }

        public void breakBed() {
            this.hasBed = false;
        }
    }

    public static final String FILE_EXTENSION = "bwmap";
    public static final String DIAMOND_GENS_KEY = "diamond_gens";
    public static final String EMERALD_GENS_KEY = "emerald_gens";
    public static final String ISLAND_GENS_KEY = "island_gens";
    public static final String BED_POSITIONS_KEY = "bed_positions";

    public static final float SPLIT_RANGE = 3f;

    private final Set<ResourceGenerator> diamondGens;
    private final Set<ResourceGenerator> emeraldGens;
    private final Multimap<DyeColourWithEmpty, IslandGenerator> islandGens;
    private final Map<DyeColor, BlockPos> bedPositions;

    public BedwarsGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull BlockPos absoluteCentrePos,
            @NotNull ServerWorld world,
            @NotNull Set<BlockPos> diamondGens,
            @NotNull Set<BlockPos> emeraldGens,
            @NotNull Set<BlockPos> islandGens,
            @NotNull Set<BlockPos> bedPositions
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, world);
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), world, diamondGens.stream().map(super::pos).collect(Collectors.toSet()), false, 30 * 20);
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), world, emeraldGens.stream().map(super::pos).collect(Collectors.toSet()), false, 30 * 20);
        this.islandGens = this.makeIslandGenerators(world, islandGens.stream().map(super::pos).collect(Collectors.toSet()), spawnPositions.stream().map(spawnPosition -> spawnPosition.withPos(super.pos(spawnPosition.pos()))).collect(Collectors.toSet()));
        this.bedPositions = this.makeBedPositions(spawnPositions, bedPositions);
    }

    /// Constructor used only for saving the map to file
    public BedwarsGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull Set<BlockPos> diamondGens,
            @NotNull Set<BlockPos> emeraldGens,
            @NotNull Set<BlockPos> islandGens,
            @NotNull Set<BlockPos> bedPositions
    ) {
        super(structure, spawnPositions, centrePos);
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), super.world, diamondGens, false, 30 * 20);
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), super.world, emeraldGens, false, 30 * 20);
        this.islandGens = this.makeIslandGenerators(super.world, islandGens, spawnPositions);
        this.bedPositions = this.makeBedPositions(spawnPositions, bedPositions);
    }

    private Map<DyeColor, BlockPos> makeBedPositions(Set<SpawnPosition> spawnPositions, Set<BlockPos> bedPositions) {
        return spawnPositions.stream().collect(Collectors.toMap(SpawnPosition::dyeColour, spawn -> findNearestBlockPos(bedPositions, spawn.pos()).get()));
    }

    private static Optional<BlockPos> findNearestBlockPos(Collection<BlockPos> positions, BlockPos origin) {
        return positions.stream().min(Comparator.comparing(pos -> pos.getSquaredDistance(origin)));
    }

    public boolean isWithinSplitRange(ServerPlayerEntity player) {
        return this.islandGens.values().stream().map(IslandGenerator::getPos).anyMatch(pos -> pos.isWithinDistance(player.getPos(), SPLIT_RANGE));
    }

    public static Optional<BedwarsGameMap> fromNbt(@NotNull NbtCompound compound, @NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        final StructureTemplateManager templateManager = world.getStructureTemplateManager();
        final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
        final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

        final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
        if (centrePosLong.isEmpty()) {
            SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
            return Optional.empty();
        }

        final Set<SpawnPosition> spawns = compound.getListOrEmpty(SpawnPosition.LIST_KEY).stream().map(element -> new SpawnPosition(element.asCompound().orElseThrow())).collect(Collectors.toSet());

        return Optional.of(new BedwarsGameMap(
                template,
                spawns,
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                world,
                getBlockPosSet(compound, DIAMOND_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load diamond gens"); return Set.of(); }),
                getBlockPosSet(compound, EMERALD_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load emerald gens"); return Set.of(); }),
                getBlockPosSet(compound, ISLAND_GENS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load island gens"); return Set.of(); }),
                getBlockPosSet(compound, BED_POSITIONS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load bed positions"); return Set.of(); })
        ));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        putBlockPosCollection(compound, DIAMOND_GENS_KEY, this.diamondGens.stream().map(ResourceGenerator::getPos).toList());
        putBlockPosCollection(compound, EMERALD_GENS_KEY, this.emeraldGens.stream().map(ResourceGenerator::getPos).toList());
        putBlockPosCollection(compound, ISLAND_GENS_KEY, this.islandGens.values().stream().map(IslandGenerator::getPos).toList());
        putBlockPosCollection(compound, BED_POSITIONS_KEY, this.bedPositions.values());

        return compound;
    }

    private Multimap<DyeColourWithEmpty, IslandGenerator> makeIslandGenerators(ServerWorld world, Set<BlockPos> islandGens, Set<SpawnPosition> teams) {
        return islandGens.stream().collect(Multimaps.toMultimap(
                genPos -> teams.stream().filter(spawn -> genPos.getSquaredDistance(spawn.pos()) < 9).findAny().map(spawn -> DyeColourWithEmpty.fromDyeColour(spawn.dyeColour())).orElse(DyeColourWithEmpty.EMPTY),
                genPos -> new IslandGenerator(world, genPos),
                MultimapBuilder.treeKeys().arrayListValues()::build)
        );
    }

    @Override
    public void tick() {
        this.islandGens.values().forEach(IslandGenerator::tick);
        this.diamondGens.forEach(ResourceGenerator::tick);
        this.emeraldGens.forEach(ResourceGenerator::tick);
    }

    public void upgradeDiamondGens(GeneratorStats stats) {
        this.diamondGens.forEach(gen -> gen.setStats(stats));
    }

    public void upgradeEmeraldGens(GeneratorStats stats) {
        this.emeraldGens.forEach(gen -> gen.setStats(stats));
    }

    public Map<DyeColor, BlockPos> getBedPositions() {
        return this.bedPositions;
    }
}
