package com.soc.game.map;

import com.soc.SocWars;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HideAndSeekGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "hsmap";
    public static final DyeColor SEEKER_COLOUR = DyeColor.RED;
    public static final DyeColor HIDER_COLOUR = DyeColor.BLUE;

    public HideAndSeekGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull BlockPos absoluteCentrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            @NotNull ServerWorld world
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, blockProtectionOverlay, world);
    }

    /// Constructor used only for saving the map to file
    public HideAndSeekGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay
    ) {
        super(structure, spawnPositions, centrePos, blockProtectionOverlay);
    }

    public static Optional<HideAndSeekGameMap> fromNbt(@NotNull NbtCompound compound, @NotNull ServerWorld world, @NotNull BlockPos centrePos) {
        final StructureTemplateManager templateManager = world.getStructureTemplateManager();
        final Optional<NbtCompound> structureCompound = compound.getCompound(STRUCTURE_KEY);
        final StructureTemplate template = structureCompound.map(templateManager::createTemplate).orElse(null);

        final Optional<Long> centrePosLong = compound.getLong(CENTRE_POS_KEY);
        if (centrePosLong.isEmpty()) {
            SocWars.LOGGER.error("Failed to load centre position for map; aborting load");
            return Optional.empty();
        }

        final Set<SpawnPosition> spawns = compound.getListOrEmpty(SpawnPosition.LIST_KEY).stream().map(element -> new SpawnPosition(element.asCompound().orElseThrow())).collect(Collectors.toSet());

        return Optional.of(new HideAndSeekGameMap(
                template,
                spawns,
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                SparseVoxelOctree.fromNbtBooleanOnly(BLOCK_PROTECTION_OVERLAY_KEY, compound),
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