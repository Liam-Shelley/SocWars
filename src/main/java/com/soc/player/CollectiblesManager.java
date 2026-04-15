package com.soc.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.SocWars;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.nCopies;

public class CollectiblesManager extends PersistentState {
    //Extraordinarily utilitous little inner class here that I would lose my mind without
    private record Entry(int id, RegistryKey<World> world, BlockPos pos) {
        public static Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("id").orElse(-1).forGetter(Entry::id),
                World.CODEC.fieldOf("world").orElse(null).forGetter(Entry::world),
                BlockPos.CODEC.fieldOf("id").orElse(null).forGetter(Entry::pos)
        ).apply(instance, Entry::new));

        private boolean isInvalid() {
            return this.id == -1 && this.world == null && this.pos == null;
        }

        private void logInvalidWarning() {
            SocWars.LOGGER.warn(
                    "Skipped loading Collectible entry as part of its data was missing: Id: {}, World: {}, Pos: {}",
                    this.id == -1 ? "INVALID" : this.id,
                    this.world == null ? "INVALID" : this.world.getValue().toString(),
                    this.pos == null ? "INVALID" : this.pos.toString()
            );
        }
    }

    public static CollectiblesManager INSTANCE = new CollectiblesManager();

    //I love codecs so much why can't they be like packet codecs?
    public static Codec<CollectiblesManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Entry.CODEC).fieldOf("Entries").orElse(List.of()).forGetter(CollectiblesManager::getEntries),
            Codec.list(Codec.INT).fieldOf("Ledger").orElse(List.of()).forGetter(CollectiblesManager::getLedger)
    ).apply(instance, CollectiblesManager::new));

    public static PersistentStateType<CollectiblesManager> STATE_TYPE = new PersistentStateType<>("", () -> INSTANCE, CODEC, null);

    public static void initialise() {
        ServerLifecycleEvents.AFTER_SAVE.register((server, flush, force) -> {
            server.getOverworld().getPersistentStateManager().set(STATE_TYPE, INSTANCE);
        });
    }

    private final List<Entry> collectibleMap;
    private final List<Integer> removedBlockLedger;

    private CollectiblesManager(List<Entry> entries, List<Integer> removedBlockLedger) {
        this.collectibleMap = rectifyEntries(entries);
        this.removedBlockLedger = removedBlockLedger;

        INSTANCE = this; //This reeks but I like its stank
    }

    private static List<Entry> rectifyEntries(List<Entry> entries) { //Not even going to worry about performance here since this list will only maybe ever be about 100 entries long at most
        if (entries.isEmpty()) return List.of();
        final int maxId = entries.stream().filter(entry -> {
            if (entry.isInvalid()) {
                entry.logInvalidWarning();
                return false;
            } else return true;
        }).mapToInt(Entry::id).max().getAsInt();

        final List<Entry> rectifiedEntries = new ArrayList<>(nCopies(maxId - 1, null));
        final Multimap<Integer, Entry> conflictingEntries = HashMultimap.create(); //TODO: Use this overengineeredness to actually write logging for duplicate entries even though it should never happen

        for (Entry entry : entries) {
            final int id = entry.id;
            if (rectifiedEntries.get(id) == null) {
                rectifiedEntries.set(id, entry);
            } else {
                conflictingEntries.put(id, rectifiedEntries.get(id));
                conflictingEntries.put(id, entry);
            }
        }

        for (int i : conflictingEntries.keySet()) {
            rectifiedEntries.set(i, null);
        }

        return rectifiedEntries;
    }

    private CollectiblesManager() {
        this.collectibleMap = new ArrayList<>();
        this.removedBlockLedger = new ArrayList<>();
    }

    private List<Entry> getEntries() {
        return this.collectibleMap;
    }

    private List<Integer> getLedger() {
        return this.removedBlockLedger;
    }

    public static void onPlacedBlock(RegistryKey<World> world, BlockPos pos) {
        INSTANCE.collectibleMap.put()
    }
}
