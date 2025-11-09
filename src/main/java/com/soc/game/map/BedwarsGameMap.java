package com.soc.game.map;

import com.google.common.collect.*;
import com.soc.SocWars;
import com.soc.nbt.SpawnPosition;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
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

    private final Set<ResourceGenerator> diamondGens;
    private final Set<ResourceGenerator> emeraldGens;
    private final Map<DyeColor, ResourceGenerator[]> islandGens;
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
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), world, diamondGens.stream().map(super::pos).collect(Collectors.toSet()), 30 * 20);
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), world, emeraldGens.stream().map(super::pos).collect(Collectors.toSet()), 30 * 20);
        this.islandGens = this.makeIslandGenerators(world, islandGens.stream().map(super::pos).collect(Collectors.toSet()), spawnPositions.stream().map(SpawnPosition::dyeColour).collect(Collectors.toSet()));
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
        this.diamondGens = ResourceGenerator.resourceGenerators(Items.DIAMOND.getDefaultStack(), world, diamondGens, 30 * 20);
        this.emeraldGens = ResourceGenerator.resourceGenerators(Items.EMERALD.getDefaultStack(), world, emeraldGens, 30 * 20);

        ImmutableMap.Builder<DyeColor, ResourceGenerator[]> builder = new ImmutableMap.Builder<>();
        for (int i = 0; i < islandGens.size(); i++) {
            builder.put(dyeColourFromOrdinal(i), new ResourceGenerator[]{new ResourceGenerator(Items.STONE.getDefaultStack(), null, islandGens.stream().toList().get(i), 30 * 20)});
        }
        this.islandGens = builder.build();

        this.bedPositions = this.makeBedPositions(spawnPositions, bedPositions); //Double check that this works
    }

    private Map<DyeColor, BlockPos> makeBedPositions(Set<SpawnPosition> spawnPositions, Set<BlockPos> bedPositions) {
        return spawnPositions.stream().collect(Collectors.toMap(SpawnPosition::dyeColour, spawn -> findNearestBlockPos(bedPositions, spawn.pos()).get()));
    }

    private static Optional<BlockPos> findNearestBlockPos(Collection<BlockPos> positions, BlockPos origin) {
        return positions.stream().min(Comparator.comparing(pos -> pos.getSquaredDistance(origin)));
    }

    public static Optional<BedwarsGameMap> loadRandomMap(@NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        Optional<File> file = AbstractGameMap.getRandomMap(FILE_EXTENSION, world, null);

        return file.flatMap(optional -> loadFromFile(file.get(), world, centrePos));
    }

    public static Optional<BedwarsGameMap> loadFromFile(File file, @NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        NbtCompound compound = null;
        try {
            compound = NbtIo.read(file.toPath());
        } catch (IOException e) {
            SocWars.LOGGER.error("Could not read compound at {}", file.getAbsolutePath());
        }

        if (compound == null) return Optional.empty();

        return fromNbt(compound, world, centrePos);
    }

    private static Optional<BedwarsGameMap> fromNbt(@NotNull NbtCompound compound, @NotNull ServerWorld world, @NotNull BlockPos centrePos) {
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
        putBlockPosCollection(compound, ISLAND_GENS_KEY, this.islandGens.values().stream().map(gens -> gens[0].getPos()).toList());
        putBlockPosCollection(compound, BED_POSITIONS_KEY, this.bedPositions.values());

        return compound;
    }


    private ImmutableMap<DyeColor, ResourceGenerator[]> makeIslandGenerators(ServerWorld world, Set<BlockPos> islandGens, Set<DyeColor> teams) {
        final List<BlockPos> islandGenList = islandGens.stream().toList(); //Should probably revisit this whole function at some point

        final List<DyeColor> allTeams = new ArrayList<>(teams);
        allTeams.addAll(Arrays.stream(new DyeColor[islandGens.size() - teams.size()]).toList());
        final List<DyeColor> allTeamList = allTeams.stream().toList();

        final ImmutableMap.Builder<DyeColor, ResourceGenerator[]> builder = ImmutableMap.builder();

        for (int i = 0; i < islandGens.size(); i++) {
            builder.put(allTeamList.get(i), new ResourceGenerator[]{
                    new ResourceGenerator(Items.IRON_INGOT.getDefaultStack().copyWithCount(4), world, islandGenList.get(i), 2 * 20),
                    new ResourceGenerator(Items.GOLD_INGOT.getDefaultStack(), world, islandGenList.get(i), 5 * 20),
                    new ResourceGenerator(Items.EMERALD.getDefaultStack(), world, islandGenList.get(i), 0),
            });
        }

        return builder.build();
    }


    @Override
    public void tick() {
        this.islandGens.forEach((team, gen) -> Arrays.stream(gen).iterator().forEachRemaining(ResourceGenerator::tick));
        this.diamondGens.forEach(ResourceGenerator::tick);
        this.emeraldGens.forEach(ResourceGenerator::tick);
    }
}
