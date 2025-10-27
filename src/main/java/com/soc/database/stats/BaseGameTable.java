package com.soc.database.stats;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class BaseGameTable extends BaseTable {
    protected int wins = 0;
    public void win() { this.wins++; }
    protected int losses = 0;
    public void lose() { this.losses++; }
    protected long xp = 0;
    public void addXp(long xp) { this.xp += xp; }

    protected BaseGameTable(ServerPlayerEntity player) {
        super(player);
    }
}
