package com.soc.game.map;

import com.soc.SocWars;
import com.soc.lib.SocWarsLib;
import com.soc.nbt.SkywarsChest;
import com.soc.nbt.SpawnPosition;
import com.soc.resourcedata.ResourceManager;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SkywarsGameMap extends AbstractGameMap {
    public static final String FILE_EXTENSION = "swmap";

    private final Set<SkywarsChest> lootChests;

    public SkywarsGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull BlockPos absoluteCentrePos,
            @NotNull ServerWorld world,
            @NotNull Set<SkywarsChest> lootChests
    ) {
        super(structure, spawnPositions, centrePos, absoluteCentrePos, world);
        this.lootChests = lootChests;
    }

    /// Constructor used only for saving the map to file
    public SkywarsGameMap(
            StructureTemplate structure,
            @NotNull Set<SpawnPosition> spawnPositions,
            @NotNull BlockPos centrePos,
            @NotNull Set<SkywarsChest> lootChests
    ) {
        super(structure, spawnPositions, centrePos);
        this.lootChests = lootChests;
    }

    public void placeLootChests() {
        this.lootChests.forEach(chest -> {
            final BlockPos chestPos = super.pos(chest.pos()).down();
            this.world.setBlockState(chestPos, Blocks.CHEST.getDefaultState().with(HorizontalFacingBlock.FACING, chest.facing()));

            final Inventory inventory = ChestBlock.getInventory((ChestBlock) Blocks.CHEST, this.world.getBlockState(chestPos), this.world, chestPos, true);
            if (inventory != null) {
                this.populateInventory(inventory, chest.tier());
            } else {
                SocWars.LOGGER.warn("Failed to populate chest at {}", chest.pos());
            }
        });
    }

    private void populateInventory(Inventory inventory, int tier) {
        inventory.clear();
        for (int i = 0; i < inventory.size(); i++) {
            final float random = this.world.random.nextFloat();
            if (random > 0.42f + tier * 0.04f) {
                final int pool = random < 0.73f + tier * 0.06f ? 0 : 1;
                final Pair<Item, Integer> item = ResourceManager.ITEM_DATA.getSkywarsItemData().getRandomItem(pool, tier - 1, this.world.random);
                inventory.setStack(i, new ItemStack(item.getLeft(), item.getRight()));
            }
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

        final Set<SpawnPosition> spawns = compound.getListOrEmpty(SpawnPosition.LIST_KEY).stream().map(element -> new SpawnPosition(element.asCompound().orElseThrow())).collect(Collectors.toSet());
        final Set<SkywarsChest> chests = compound.getListOrEmpty(SkywarsChest.LIST_KEY).stream().map(element -> new SkywarsChest(element.asCompound().orElseThrow())).collect(Collectors.toSet());

        return Optional.of(new SkywarsGameMap(
                template,
                spawns,
                BlockPos.fromLong(centrePosLong.get()),
                centrePos,
                world,
                chests
        ));
    }

    @Override
    public NbtCompound toNbt(NbtCompound compound) {
        super.toNbt(compound);

        compound.put(SkywarsChest.LIST_KEY, getChestsAsNbt());

        return compound;
    }

    private NbtList getChestsAsNbt() {
        NbtList chests = new NbtList();
        this.lootChests.forEach(chest -> chests.add(chest.toNbt()));
        return chests;
    }

    @Override
    public void tick() {}
}