package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.List;

public class DeathNoteState extends PersistentState {
	public static void initialise() {}

	public DeathNoteState(List<String> names) {
		this.names = new ArrayList<>(names);
	}

	public DeathNoteState() {
		this.names = new ArrayList<>();
	}

	private final List<String> names;

	public static final Codec<DeathNoteState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.list(Codec.STRING).fieldOf("names").forGetter(state -> state.names)
	).apply(instance, DeathNoteState::new));

	public static PersistentStateType<DeathNoteState> STATE_TYPE = new PersistentStateType<>("death_note", DeathNoteState::new, CODEC, null);

	public static DeathNoteState getPersistentState(ServerWorld serverWorld) {
		return serverWorld.getServer().getOverworld().getPersistentStateManager().getOrCreate(STATE_TYPE);
	}

	public static List<String> getNames(ServerWorld serverWorld) {
		return getPersistentState(serverWorld).names;
	}

	public static void addName(ServerPlayerEntity player) {
		final DeathNoteState state = getPersistentState(player.getWorld());
		state.names.add(player.getNameForScoreboard());
		state.markDirty();
	}
}
