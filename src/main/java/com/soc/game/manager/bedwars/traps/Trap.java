package com.soc.game.manager.bedwars.traps;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.shopitems.DisplayShopItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public abstract class Trap {
    public static final String KEY = "trap";

    private final Identifier id;
    private final ItemStack icon;
    private final int cooldownTime;

    public Trap(Identifier id, ItemStack icon, int time) {
        this.id = id;
        this.icon = icon;
        this.cooldownTime = time;
    }

    public Trap(String id, ItemStack icon, int time) {
        this(Identifier.of(SocWars.MOD_ID, id), icon, time);
    }

    public abstract void trigger(Vec3d pos, List<ServerPlayerEntity> team, List<ServerPlayerEntity> enemies);

    public final int getCooldownTime() {
        return this.cooldownTime;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public final Identifier getId() {
        return this.id;
    }

    public DisplayShopItem getDisplayShopItem() {
        return new DisplayShopItem(this.getIcon());
    }
}
