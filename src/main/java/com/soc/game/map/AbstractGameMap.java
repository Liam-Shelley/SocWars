package com.soc.game.map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.soc.SocWars;
import com.soc.lib.Coroutine;
import com.soc.lib.Coroutines;
import com.soc.lib.SparseVoxelOctree;
import com.soc.nbt.SpawnPosition;
import com.soc.networking.s2c.BlockProtectionPayload;
import com.soc.networking.s2c.SetAnglesPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.iterateInCube;

public abstract class AbstractGameMap {
    public static final String STRUCTURE_KEY = "structure";
    public static final String CENTRE_POS_KEY = "centre_positions";
    public static final String BLOCK_PROTECTION_OVERLAY_KEY = "block_protection_overlay";

    private static final int Y_CLEARING_BUFFER = 64;
    private static final int XZ_CLEARING_BUFFER = 64;

    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    protected final StructureTemplate structure;
    public final float size;

    protected final BlockPos centrePos;
    protected final BlockPos absoluteCentrePos;
    protected final Multimap<DyeColor, BlockPos> spawnPositions;
    @Nullable protected final SparseVoxelOctree<Boolean> blockProtectionOverlay;
    @Nullable protected final BlockProtectionPayload blockProtectionPacket; //Cache me outside how bout dat

    protected final ServerWorld world;

    protected int tick;

    public AbstractGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            BlockPos absoluteCentrePos,
            @Nullable SparseVoxelOctree<Boolean> blockProtectionOverlay,
            ServerWorld world
    ) {
        this.structure = structure;
        this.size = structure.getSize().getChebyshevDistance(Vec3i.ZERO);
        this.spawnPositions = spawnPositions.stream().collect(Multimaps.toMultimap(SpawnPosition::dyeColour, SpawnPosition::pos, HashMultimap::create));
        this.centrePos = centrePos.toImmutable();
        this.absoluteCentrePos = absoluteCentrePos;
        this.blockProtectionOverlay = blockProtectionOverlay;
        this.blockProtectionPacket = blockProtectionOverlay == null ? null : new BlockProtectionPayload(blockProtectionOverlay, this.getOrigin());
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
                BlockPos.ORIGIN,
                blockProtectionOverlay,
                null
        );
    }

    public abstract void tick();

    public void spreadPlayers(Multimap<DyeColor, UUID> teams) {
        teams.keySet().forEach(team -> this.spreadTeam(teams, team));
    }

    protected void spreadTeam(Multimap<DyeColor, UUID> teams, DyeColor team) {
        teams.get(team).forEach(uuid -> {
            final ServerPlayerEntity player = (ServerPlayerEntity)this.world.getPlayerByUuid(uuid);
            if (player == null) return;

            final Optional<BlockPos> pos = this.getSpawnPosition(team);
            pos.ifPresentOrElse(
                    destPos -> {
                        player.requestTeleport(destPos.getX() + 0.5d, destPos.getY(), destPos.getZ() + 0.5d);
                        final Vec3i vectorToCentre = this.absoluteCentrePos.subtract(destPos);
                        ServerPlayNetworking.send(player, new SetAnglesPayload(player.getId(), (float) Math.atan2(vectorToCentre.getZ(), vectorToCentre.getX()) * 57.295776f - 90f, 0f));
                    },
                    () -> player.sendMessage(Text.literal("Go yell at Liam for screwing up the spreadPlayers method"))
            );
        });
    }

    public Optional<BlockPos> getSpawnPosition(DyeColor team) {
        final List<BlockPos> positions = new ArrayList<>(this.spawnPositions.get(team));
        if (positions.isEmpty()) return Optional.empty();

        return Optional.of(this.pos(positions.get(this.world.random.nextBetween(0, positions.size() - 1))));
    }

    public final Collection<BlockPos> getSpawnPositions(DyeColor team) {
        return this.spawnPositions.get(team);
    }

    public NbtCompound toNbt(NbtCompound compound) {
        compound.put(STRUCTURE_KEY, this.structure.writeNbt(new NbtCompound()));
        compound.put(SpawnPosition.LIST_KEY, this.getSpawnsAsNbt());
        compound.putLong(CENTRE_POS_KEY, this.centrePos.asLong());

        if (this.blockProtectionOverlay != null) this.blockProtectionOverlay.writeToNbtBooleanOnly(BLOCK_PROTECTION_OVERLAY_KEY, compound);

        return compound;
    }

    private NbtList getSpawnsAsNbt() {
        final NbtList spawns = new NbtList();
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
        final File[] files = getMapDirectory().listFiles(file -> file.toString().endsWith("." + fileExtension));

        return (files != null) ? files : new File[0];
    }

    public static Optional<File> getRandomMap(String fileExtension, World world, @Nullable String preferred_map) {
        final File[] maps = getMaps(fileExtension);
        if (maps.length == 0) return Optional.empty();

        return Optional.of(maps[world.random.nextBetween(0, maps.length - 1)]);
    }

    public static <T extends AbstractGameMap> Optional<T> loadRandomMap(ServerWorld world, BlockPos centrePos, FromNbtFunction<T> fromNbtFunction, String fileExtension) {
        final Optional<File> optional = AbstractGameMap.getRandomMap(fileExtension, world, null);

        return optional.flatMap(file -> loadFromFile(file, world, centrePos, fromNbtFunction));
    }

    public static <T extends AbstractGameMap> Optional<T> loadFromFile(File file, ServerWorld world, BlockPos centrePos, FromNbtFunction<T> fromNbtFunction) {
        try {
            final NbtCompound compound = NbtIo.read(file.toPath());
            return compound == null ? Optional.empty() : fromNbtFunction.fromNbt(compound, world, centrePos);
        } catch (IOException e) {
            SocWars.LOGGER.error("Could not read compound at {}", file.getAbsolutePath());
            return Optional.empty();
        }
    }

    public static Stack<UUID> getRandomPlayerStack(Collection<ServerPlayerEntity> players) {
        final Stack<UUID> playerStack = new Stack<>();
        Collections.shuffle(new ArrayList<>(players));
        for (ServerPlayerEntity player : players) {
            playerStack.add(player.getUuid());
        }

        return playerStack;
    }

    public final BlockPos pos(BlockPos pos) {
        return pos.add(this.absoluteCentrePos);
    }

    public final List<BlockPos> poss(Collection<BlockPos> poss) {
        return poss.stream().map(pos -> pos.add(this.absoluteCentrePos)).toList();
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

    public final void destroyMap(boolean immediate) {
        final BlockPos minPos = this.getOrigin();
        final BlockPos maxPos = minPos.add(this.structure.getSize());

        final AtomicInteger y = new AtomicInteger(Math.min(maxPos.getY() + Y_CLEARING_BUFFER, this.world.getTopYInclusive()));
        final int minX = minPos.getX() - XZ_CLEARING_BUFFER;
        final int maxX = maxPos.getX() + XZ_CLEARING_BUFFER;
        final int minZ = minPos.getZ() - XZ_CLEARING_BUFFER;
        final int maxZ = maxPos.getZ() + XZ_CLEARING_BUFFER;

        if (immediate) {
            iterateInCube(new Vec3i(minX, this.world.getBottomY(), minZ), new Vec3i(maxX, y.get(), maxZ), pos -> this.world.setBlockState(pos, Blocks.AIR.getDefaultState()));
        } else {
            Coroutines.getInstance().startCoroutine(new Coroutine<>(this, map -> {
                iterateInCube(new Vec3i(minX, y.get() - 1, minZ), new Vec3i(maxX, y.get(), maxZ), pos -> this.world.setBlockState(pos, Blocks.AIR.getDefaultState()));
                return y.getAndDecrement() < this.world.getBottomY();
            }));
        }

        this.world.getOtherEntities(null, new Box(minPos.toCenterPos(), maxPos.toCenterPos()), entity -> entity.getType() != EntityType.PLAYER).forEach(entity -> entity.kill(this.world));
    }

    public final BlockPos getCentrePos() {
        return this.absoluteCentrePos;
    }

    public Vec3d getRespawnSpectatorPos() {
        return this.absoluteCentrePos.up(this.structure.getSize().getY() + 15).toCenterPos();
    }

    public void spawnCages(boolean place, DyeColor... teams) {
        final Consumer<BlockPos> function = place ? pos -> {
                if (this.world.isAir(pos)) this.world.setBlockState(pos, Blocks.BARRIER.getDefaultState());
        } : pos -> {
                if (this.world.getBlockState(pos).isOf(Blocks.BARRIER)) this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
        };

        this.spawnPositions.entries().forEach(position -> {
            if (teams.length == 0 || Arrays.stream(teams).anyMatch(team -> team == position.getKey())) {
                final BlockPos pos = this.pos(position.getValue());
                for (Direction direction : HORIZONTAL_DIRECTIONS) {
                    final BlockPos offset = pos.offset(direction);
                    function.accept(offset);
                    function.accept(offset.up());
                }
            }
        });
    }

    public boolean isBlockProtected(BlockPos pos) {
        return this.blockProtectionOverlay != null && this.blockProtectionOverlay.get(pos, this.getOrigin());
    }

    public @Nullable BlockProtectionPayload getBlockProtectionPacket() {
        return this.blockProtectionPacket;
    }

    public Optional<Map.Entry<DyeColor, BlockPos>> getClosestSpawn(BlockPos pos) {
        return this.spawnPositions.entries().stream().min(Comparator.comparingDouble(entry -> entry.getValue().getSquaredDistance(this.pos(pos))));
    }
}