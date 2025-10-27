package com.soc.database.stats;

import com.soc.SocWars;
import net.minecraft.server.network.ServerPlayerEntity;

public class LobbyTable extends BaseTable {
    protected boolean[] collectibles = new boolean[0];
    public void collectCollectible(short id) {
        SocWars.LOGGER.warn("Collectible stats not yet implemented");
    }
    protected long timeSpent = 0;
    public void spendTime(long time) { this.timeSpent += time; }

    public LobbyTable(ServerPlayerEntity player) {
        super(player);
    }

    public LobbyTable() {
        this(null);
    }

    @Override
    public String getTableName() {
        return "LOBBY";
    }
}
