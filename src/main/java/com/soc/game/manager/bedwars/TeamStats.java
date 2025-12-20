package com.soc.game.manager.bedwars;

public class TeamStats {
    private boolean hasBed = true;

    public boolean hasBed() {
        return this.hasBed;
    }

    public boolean breakBed() {
        if (!this.hasBed) return false;

        this.hasBed = false;
        return true;
    }
}
