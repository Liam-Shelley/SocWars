package com.soc.game.map;

import com.google.common.collect.Multimap;
import com.soc.SocWars;
import com.soc.lib.Coroutine;
import com.soc.lib.Coroutines;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.iterateInCube;

public abstract class AbstractGameMap {
    public static final String STRUCTURE_KEY = "structure";
    public static final String CENTRE_POS_KEY = "centre_positions";
    public static final String BLOCK_PROTECTION_OVERLAY_KEY = "block_protection_overlay";

    private static final int Y_CLEARING_BUFFER = 64;
    private static final int XZ_CLEARING_BUFFER = 64;

    protected final StructureTemplate structure;
    protected final BlockPos centrePos;
    protected final BlockPos absoluteCentrePos;
    protected final Map<DyeColor, BlockPos> spawnPositions;
    protected final SparseVoxelOctree<Boolean> blockProtectionOverlay;

    protected final ServerWorld world;
    protected int tick;

    public AbstractGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            BlockPos absoluteCentrePos,
            SparseVoxelOctree<Boolean> blockProtectionOverlay,
            ServerWorld world
    ) {
        this.structure = structure;
        this.spawnPositions = spawnPositions.stream().collect(Collectors.toMap(SpawnPosition::dyeColour, SpawnPosition::pos));
        this.centrePos = centrePos.toImmutable();
        this.absoluteCentrePos = absoluteCentrePos;
        this.blockProtectionOverlay = blockProtectionOverlay;
        this.world = world;
    }

    /// Constructor used only for saving the map to file
    public AbstractGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            SparseVoxelOctree<Boolean> blockProtectionOverlay
    ) {
        this(
                structure,
                spawnPositions,
                centrePos.toImmutable(),
                new BlockPos(0, 0, 0), blockProtectionOverlay,
                null
        );
    }

    public abstract void tick();

    public final void spreadPlayers(Multimap<DyeColor, UUID> teams) {
        teams.forEach((team, uuid) -> {
            final ServerPlayerEntity player = (ServerPlayerEntity)this.world.getPlayerByUuid(uuid);
            final BlockPos rawPos = this.spawnPositions.get(team);
            if (rawPos == null) {
                player.sendMessage(Text.literal("Go yell at Liam for screwing up the spreadPlayers() method"));
            } else {
                final BlockPos pos = this.pos(rawPos);

                player.requestTeleport(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
            }
        });
    }

    public NbtCompound toNbt(NbtCompound compound) {
        compound.put(STRUCTURE_KEY, this.structure.writeNbt(new NbtCompound()));
        compound.put(SpawnPosition.LIST_KEY, this.getSpawnsAsNbt());
        compound.putLong(CENTRE_POS_KEY, this.centrePos.asLong());

        if (this.blockProtectionOverlay != null) this.blockProtectionOverlay.writeToNbtBooleanOnly(BLOCK_PROTECTION_OVERLAY_KEY, compound);

        return compound;
    }

    private NbtList getSpawnsAsNbt() {
        NbtList spawns = new NbtList();
        this.spawnPositions.forEach((colour, pos) -> spawns.add(new SpawnPosition(pos, colour.getIndex()).toNbt()));
        return spawns;
    }

    public static File getMapDirectory() {
        final File file = Path.of(
                FabricLoader.getInstance().getConfigDir().toString(),
                SocWars.MOD_ID,
                "maps"
        ).toFile();

        //Ensure that the folder exists before returning
        final boolean madeFileDir = file.mkdirs();
        SocWars.LOGGER.info(madeFileDir ? "Created maps file directory" : "Failed to create maps file directory " + (file.exists() ? "as it already exists" : "because screw you I guess?"));

        return file;
    }

    public static File[] getMaps(String fileExtension) {
        final var files = getMapDirectory().listFiles(file -> file.toString().endsWith("." + fileExtension));

        return (files != null) ? files : new File[0];
    }

    public static Optional<File> getRandomMap(String fileExtension, World world, @Nullable String preferred_map) {
        final File[] maps = getMaps(fileExtension);
        if (maps.length == 0) return Optional.empty();

        return Optional.of(maps[world.random.nextBetween(0, maps.length - 1)]);
    }

    public static <T extends AbstractGameMap> Optional<T> loadRandomMap(@NotNull ServerWorld world, @NotNull BlockPos centrePos, FromNbtFunction<T> fromNbtFunction, @NotNull String fileExtension) {
        final Optional<File> file = AbstractGameMap.getRandomMap(fileExtension, world, null);

        return file.flatMap(optional -> loadFromFile(file.get(), world, centrePos, fromNbtFunction));
    }

    public static <T extends AbstractGameMap> Optional<T> loadFromFile(File file, @NotNull ServerWorld world, @NotNull BlockPos centrePos, FromNbtFunction<T> fromNbtFunction) {
        try {
            final NbtCompound compound = NbtIo.read(file.toPath());
            return compound == null ? Optional.empty() : fromNbtFunction.fromNbt(compound, world, centrePos);
        } catch (IOException e) {
            SocWars.LOGGER.error("Could not read compound at {}", file.getAbsolutePath());
            return Optional.empty();
        }
    }

    public static Stack<UUID> getRandomPlayerStack(Collection<UUID> players) {
        final Stack<UUID> playerStack = new Stack<>();
        Collections.shuffle((ArrayList<?>) new ArrayList<>(players).clone());
        playerStack.addAll(players);

        return playerStack;
    }

    public final BlockPos pos(BlockPos pos) {
        return pos.add(this.absoluteCentrePos);
    }

    public final BlockPos getOrigin() {
        return this.absoluteCentrePos.subtract(this.centrePos);
    }

    public final Set<DyeColor> getTeamColours() {
        return this.spawnPositions.keySet();
    }

    public void placeMap() {
        this.structure.place(this.world, this.getOrigin(), this.absoluteCentrePos, new StructurePlacementData(), this.world.random, Block.NOTIFY_LISTENERS);
    }

    //Should probably optimise this at some point
    public final void destroyMap() {
        final BlockPos minPos = this.getOrigin();
        final BlockPos maxPos = minPos.add(this.structure.getSize());

        final AtomicInteger y = new AtomicInteger(Math.min(maxPos.getY() + Y_CLEARING_BUFFER, this.world.getTopYInclusive()));
        final int minX = minPos.getX() - XZ_CLEARING_BUFFER;
        final int maxX = maxPos.getX() + XZ_CLEARING_BUFFER;
        final int minZ = minPos.getZ() - XZ_CLEARING_BUFFER;
        final int maxZ = maxPos.getZ() + XZ_CLEARING_BUFFER;

        Coroutines.getInstance().startCoroutine(new Coroutine<>(this, map -> {
            iterateInCube(new BlockPos(minX, y.get() - 1, minZ), new BlockPos(maxX, y.get(), maxZ), pos -> this.world.setBlockState(pos, Blocks.AIR.getDefaultState()));
            return y.getAndDecrement() < this.world.getBottomY();
        }));

        this.world.getOtherEntities(null, new Box(minPos.toCenterPos(), maxPos.toCenterPos()), entity -> entity.getType() != EntityType.PLAYER).forEach(entity -> entity.kill(this.world));
    }

    public final BlockPos getSpawnPosition(DyeColor team) {
        final BlockPos pos = this.spawnPositions.get(team);
        if (pos == null) throw new IllegalStateException("Tried to access spawn position for team that does not exist");

        return this.pos(pos);
    }

    public final BlockPos getCentrePos() {
        return this.absoluteCentrePos;
    }

    public Vec3d getRespawnSpectatorPos() {
        return this.absoluteCentrePos.up(this.structure.getSize().getY() + 15).toCenterPos();
    }

    public void spawnCages(boolean place) {
        final BiConsumer<Direction, BlockPos> function = place ? (direction, pos) -> {
                final BlockPos currentPos = pos.offset(direction);
                if (this.world.isAir(currentPos)) this.world.setBlockState(currentPos, Blocks.BARRIER.getDefaultState());
        } : (direction, pos) -> {
                final BlockPos currentPos = pos.offset(direction);
                if (this.world.getBlockState(currentPos).isOf(Blocks.BARRIER)) this.world.setBlockState(currentPos, Blocks.AIR.getDefaultState());
        };

        this.spawnPositions.values().forEach(position -> Arrays.stream(Direction.values()).filter(direction -> direction.getAxis().isHorizontal()).forEach(direction -> {
            function.accept(direction, this.pos(position));
            function.accept(direction, this.pos(position).up());
        }));
    }

    public final boolean isBlockProtected(BlockPos pos) {
        return this.blockProtectionOverlay != null && this.blockProtectionOverlay.get(pos, this.getOrigin());
    }
}
