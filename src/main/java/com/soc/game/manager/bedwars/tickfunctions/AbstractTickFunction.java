package com.soc.game.manager.bedwars.tickfunctions;

import com.soc.SocWars;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractTickFunction {
    public static final String KEY = "tick_function";

    private final Identifier id;
    private final ItemStack icon;

    public AbstractTickFunction(Identifier id, ItemStack icon) {
        this.id = id;
        this.icon = icon;
    }

    public AbstractTickFunction(String id, ItemStack icon) {
        this(Identifier.of(SocWars.MOD_ID, id), icon);
    }

    public void tick(Vec3d pos, List<ServerPlayerEntity> team, int tier, World world) {}

    public void slowTick(Vec3d pos, List<ServerPlayerEntity> team, int tier, World world) {}

    public ItemStack getIcon() {
        return this.icon;
    }

    public final Identifier getId() {
        return this.id;
    }

    public MutableText getName() {
        return Text.translatable("tick_function." + this.getId().getPath());
    }
}
