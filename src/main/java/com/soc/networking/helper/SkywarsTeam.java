package com.soc.networking.helper;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.UUID;

public class SkywarsTeam {
    public static final PacketCodec<RegistryByteBuf, SkywarsTeam> PACKET_CODEC = PacketCodec.tuple(
            com.soc.networking.PacketCodecs.UUID, SkywarsTeam::getPlayer,
            PacketCodecs.INTEGER, team -> team.lives,
            SkywarsTeam::new
    );

    private final UUID player;
    private int lives;

    public SkywarsTeam(UUID player, int lives) {
        this.player = player;
        this.lives = lives;
    }

    public boolean isAlive() {
        return this.lives > 0;
    }

    public void eliminate() {
        this.lives = 0;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getLives() {
        return this.lives;
    }

    public UUID getPlayer() {
        return player;
    }
}
