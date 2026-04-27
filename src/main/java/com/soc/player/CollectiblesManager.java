package com.soc.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.SocWars;
import com.soc.blocks.blockentities.CollectibleBlockEntity;
import com.soc.events.ModEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.nCopies;

public class CollectiblesManager extends PersistentState {
    private record Entry(int id, RegistryKey<World> world, BlockPos pos) {
        public static Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("id").orElse(-1).forGetter(Entry::id),
                World.CODEC.fieldOf("world").orElse(null).forGetter(Entry::world),
                BlockPos.CODEC.fieldOf("pos").orElse(null).forGetter(Entry::pos)
        ).apply(instance, Entry::new));

        private boolean isValid() {
            return this.id != -1 && this.world != null && this.pos != null;
        }

        private boolean isInvalid() {
            return this.id == -1 && this.world == null && this.pos == null;
        }

        @Override
        public String toString() {
            return String.format("Id: %s, World: %s, Pos: %s",
                    this.id == -1 ? "INVALID" : this.id,
                    this.world == null ? "INVALID" : this.world.getValue().toString(),
                    this.pos == null ? "INVALID" : this.pos.toString()
            );
        }

        private boolean matches(RegistryKey<World> world, BlockPos pos) {
            return this.isValid() && this.world == world && this.pos.equals(pos);
        }
    }

    //I love codecs so much why can't they be like packet codecs?
    public static Codec<CollectiblesManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Entry.CODEC).fieldOf("entries").orElse(new ArrayList<>()).forGetter(CollectiblesManager::getEntries),
            Codec.list(Codec.INT).fieldOf("ledger").orElse(new ArrayList<>()).forGetter(CollectiblesManager::getLedger)
    ).apply(instance, CollectiblesManager::new));

    public static PersistentStateType<CollectiblesManager> STATE_TYPE = new PersistentStateType<>("collectible_block_tracking", CollectiblesManager::new, CODEC, null);

    public static void initialise() {}

    private final List<Entry> collectibleLedger;
    private final List<Integer> removedBlockLedger;

    private CollectiblesManager(List<Entry> entries, List<Integer> removedBlockLedger) {
        this.collectibleLedger = rectifyEntries(entries);
        this.removedBlockLedger = new ArrayList<>(removedBlockLedger);
    }

    private static List<Entry> rectifyEntries(List<Entry> entries) { //Not even going to worry about performance here since this list will only maybe ever be about 100 entries long at most
        if (entries.isEmpty()) return new ArrayList<>();
        final int maxId = entries.stream().filter(entry -> {
            if (entry.isInvalid()) {
                SocWars.LOGGER.warn("Skipped loading Collectible entry as part of its data was missing: {}", entry);
                return false;
            } else return true;
        }).mapToInt(Entry::id).max().orElse(-1);

        final List<Entry> rectifiedEntries = new ArrayList<>(nCopies(maxId + 1, null));
        final Multimap<Integer, Entry> conflictingEntries = HashMultimap.create();

        for (Entry entry : entries) {
            final int id = entry.id;
            if (rectifiedEntries.get(id) == null) {
                rectifiedEntries.set(id, entry);
            } else {
                conflictingEntries.put(id, rectifiedEntries.get(id));
                conflictingEntries.put(id, entry);
            }
        }

        for (int id : conflictingEntries.keySet()) {
            rectifiedEntries.set(id, null);
            final String conflictingEntriesString = conflictingEntries.get(id).stream().map(Entry::toString).reduce((a, b) -> a + ", " + b).orElse("");
            SocWars.LOGGER.warn("Skipped loading all Collectible entries for Id: {} as there were duplicates: {}", id, conflictingEntriesString);
        }

        return rectifiedEntries;
    }

    private CollectiblesManager() {
        this.collectibleLedger = new ArrayList<>();
        this.removedBlockLedger = new ArrayList<>();
    }

    private List<Entry> getEntries() {
        return this.collectibleLedger.stream().filter(entry -> entry != null && entry.isValid()).toList();
    }

    public int getNumCollectibles() {
        return this.collectibleLedger.size();
    }

    private List<Integer> getLedger() {
        return this.removedBlockLedger;
    }

    public static int addCollectibleBlock(CollectibleBlockEntity blockEntity) {
        if (!(blockEntity.getWorld() instanceof ServerWorld serverWorld)) return -1;

        return getPersistentState(serverWorld).addCollectibleBlock(blockEntity.getId(), serverWorld, blockEntity.getPos());
    }

    private int addCollectibleBlock(int tryId, ServerWorld world, BlockPos pos) {
        if (tryId >= 0 && tryId < this.collectibleLedger.size() && this.collectibleLedger.get(tryId) != null && this.collectibleLedger.get(tryId).matches(world.getRegistryKey(), pos)) {
            return tryId;
        }

        final int id = this.getNextFreeId();

        if (this.removedBlockLedger.contains(id)) {
            ModEvents.ON_COLLECTIBLE_BLOCK_REPLACED.invoker().onCollectibleBlockReplaced(id, world);
            this.removedBlockLedger.remove((Integer)id); //Please don't optimise away and screw this up
        }

        while (id >= this.collectibleLedger.size()) this.collectibleLedger.add(null);

        this.collectibleLedger.set(id, new Entry(id, world.getRegistryKey(), pos));

        this.markDirty();

        return id;
    }

    private int getNextFreeId() {
        for (int i = 0; i < this.collectibleLedger.size(); i++) {
            if (this.collectibleLedger.get(i) == null) {
                return i;
            }
        }
        return this.collectibleLedger.size();
    }

    public static void removeCollectibleBlock(int id, ServerWorld serverWorld, BlockPos pos) {
        getPersistentState(serverWorld).removeCollectibleBlock(id, serverWorld.getRegistryKey(), pos);
    }

    private void removeCollectibleBlock(int id, RegistryKey<World> world, BlockPos pos) {
        if (this.collectibleLedger.size() <= id) return;

        if (this.collectibleLedger.get(id) != null && !this.collectibleLedger.get(id).matches(world, pos)) {
            SocWars.LOGGER.warn("Removed collectible block: {} even though it did not match the current entry with the same id: {}. ", new Entry(id, world, pos), this.collectibleLedger.get(id));
        }
        this.collectibleLedger.set(id, null);
        this.removedBlockLedger.add(id);

        this.markDirty();
    }

    public static CollectiblesManager getPersistentState(ServerWorld serverWorld) {
        return serverWorld.getServer().getOverworld().getPersistentStateManager().getOrCreate(STATE_TYPE);
    }
}