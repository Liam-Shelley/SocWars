package com.soc.database.stats;

import net.minecraft.server.network.ServerPlayerEntity;

public class SkywarsTable extends CombatTable {
    protected int t1chestsOpened = 0;
    public void openT1Chest() { this.t1chestsOpened++; }
    protected int t2chestsOpened = 0;
    public void openT2Chest() { this.t2chestsOpened++; }
    protected int t3chestsOpened = 0;
    public void openT3Chest() { this.t3chestsOpened++; }

    protected int voidDeaths = 0;
    public void fallInVoid() { this.voidDeaths++; }

    public SkywarsTable(ServerPlayerEntity player) {
        super(player);
    }

    public SkywarsTable() {
        this(null);
    }

    @Override
    public String getTableName() {
        return "SKYWARS";
    }
}
