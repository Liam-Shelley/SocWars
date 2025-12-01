package com.soc.database.stats;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class BedwarsTable extends CombatTable {
    protected int finalKills = 0;
    public void grantFinalKill() { this.finalKills++; }
    protected int finalDeaths = 0;
    public void grantFinalDeath() { this.finalDeaths++; }

    protected int bedsBroken = 0;
    public void grantBedBreak() { this.bedsBroken++; }
    protected int bedsLost = 0;
    public void loseBed() { this.bedsLost++; }

    protected int iron = 0;
    public void collectIron(int count) { this.iron += count; }
    protected int gold = 0;
    public void collectGold(int count) { this.gold += count; }
    protected int diamonds = 0;
    public void collectDiamonds(int count) { this.diamonds += count; }
    protected int emeralds = 0;
    public void collectEmeralds(int count) { this.emeralds += count; }
    public void collectItem(ItemStack stack) {
        if (stack.isOf(Items.IRON_INGOT)) this.collectIron(stack.getCount());
        if (stack.isOf(Items.GOLD_INGOT)) this.collectGold(stack.getCount());
        if (stack.isOf(Items.DIAMOND)) this.collectDiamonds(stack.getCount());
        if (stack.isOf(Items.EMERALD)) this.collectEmeralds(stack.getCount());
    }

    protected int ironSpent = 0;
    public void spendIron(int count) { this.ironSpent += count; }
    protected int goldSpent = 0;
    public void spendGold(int count) { this.goldSpent += count; }
    protected int diamondsSpent = 0;
    public void spendDiamonds(int count) { this.diamondsSpent += count; }
    protected int emeraldsSpent = 0;
    public void spendEmeralds(int count) { this.emeraldsSpent += count; }

    protected int voidDeaths = 0;
    public void fallInVoid() { this.voidDeaths++; }

    public BedwarsTable(ServerPlayerEntity player) {
        super(player);
    }

    public BedwarsTable() {
        this(null);
    }

    @Override
    public String getTableName() {
        return "BEDWARS";
    }
}
