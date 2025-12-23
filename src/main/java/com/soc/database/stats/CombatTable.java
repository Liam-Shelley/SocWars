package com.soc.database.stats;

import java.util.UUID;

public abstract class CombatTable extends BaseGameTable {
    protected int kills = 0;
    public void grantKill() { this.kills++; }
    protected int deaths = 0;
    public void grantDeath() { this.deaths++; }

    protected long damageDealt = 0;
    public void dealDamage(int damage) { this.damageDealt += damage; }
    protected long damageTaken = 0;
    public void takeDamage(int damage) { this.damageTaken += damage; }

    protected int arrowsShot = 0;
    public void shootArrow() { this.arrowsShot++; }
    protected int arrowsHit = 0;
    public void hitArrow() { this.arrowsHit++; }

    protected int fireballsShot = 0;
    public void shootFireball() { this.fireballsShot++; }

    protected CombatTable(UUID player) {
        super(player);
    }
}
