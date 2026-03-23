package com.soc.database.stats;

import java.util.UUID;

public class HideAndSeekTable extends BaseGameTable {
    private int finds = 0;
    public void grantFind() {
        this.finds++;
    }
    private int founds = 0;
    public void grantFound() {
        this.founds++;
    }

    public HideAndSeekTable(UUID player) {
        super(player);
    }
    public HideAndSeekTable() {
        this(null);
    }

    @Override
    public String getTableName() {
        return "HIDEANDSEEK";
    }
}
