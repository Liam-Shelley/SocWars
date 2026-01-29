package com.soc.game.map;

import com.soc.nbt.SkywarsChest;
import net.minecraft.util.math.Direction;

public class IngameSkywarsChest {
    private final int tier;
    private final Direction facing;
    private boolean opened = false;

    public IngameSkywarsChest(int tier, Direction facing) {
        this.tier = tier;
        this.facing = facing;
    }

    public IngameSkywarsChest(SkywarsChest chest) {
        this.tier = chest.tier();
        this.facing = chest.facing();
    }

    public int getTier() {
        return this.tier + 1; //I made a stupid off by one error early on and now I will hate myself forever
    }

    public Direction getFacing() {
        return this.facing;
    }

    public boolean open() {
        if (this.opened) return false;

        this.opened = true;
        return true;
    }
}
