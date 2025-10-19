package com.soc.game.map;

import com.google.common.collect.ImmutableMap;
import com.soc.SocWars;
import com.soc.lib.SocWarsLib;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;
import static com.soc.lib.SocWarsLib.putBlockPosCollection;

public class SkywarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "swmap";

    public SkywarsGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull BlockPos absoluteCentrePos,
            @NotNull ServerWorld world
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, world);
    }

    /// Constructor used only for saving the map to file
    public SkywarsGameMap(
            StructureTemplate structure,
            @NotNull ImmutableMap<DyeColor, BlockPos> spawnPositions,
            @NotNull BlockPos centrePos
    ) {
        super(structure, spawnPositions, centrePos);
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

        final Set<BlockPos> spawn_positions = getBlockPosSet(compound, SPAWN_POSITIONS_KEY).orElseGet(() -> { SocWars.LOGGER.error("Failed to load spawn position positions"); return Set.of(); });
        final Set<DyeColor> spawn_teams = Arrays.stream(compound.getIntArray(SPAWN_TEAMS_KEY).orElse(new int[0])).mapToObj(SocWarsLib::dyeColourFromOrdinal).collect(Collectors.toSet());

        return Optional.of(new SkywarsGameMap(
                template,
                mapFromCollections(spawn_teams, spawn_positions),
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                world
        ));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        return compound;
    }

    @Override
    public void tick() {}
}