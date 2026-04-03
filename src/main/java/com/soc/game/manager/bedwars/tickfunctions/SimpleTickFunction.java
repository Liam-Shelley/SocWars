package com.soc.game.manager.bedwars.tickfunctions;

import com.soc.networking.s2c.BatchParticlePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.soc.game.manager.bedwars.tickfunctions.TickFunctions.register;
import static com.soc.lib.SocWarsLib.randomCentredVec3d;

public class SimpleTickFunction extends AbstractTickFunction {
    private static final Vec3d UP_ONE_TENTH = new Vec3d(0d, 0.1d, 0d);

    public static void initialise() {}

    public static final AbstractTickFunction HEAL_POOL = register(new SimpleTickFunction("heal_pool", PotionContentsComponent.createStack(Items.POTION, Potions.HEALING), (pos, team, tier, world) -> {
        final List<Vec3d> positions = new ArrayList<>(); //Optimise this garbage at some point hey //nvm the managers take up like 0.5% of the tick time I don't need to optimise this
        for (int i = 0; i < (tier << 2); i++) {
            positions.add(pos.add(randomCentredVec3d(world.random, 20)));
        }

        for (ServerPlayerEntity player : team) {
            ServerPlayNetworking.send(player, new BatchParticlePayload(ParticleTypes.HAPPY_VILLAGER, positions, UP_ONE_TENTH));
        }

    }, (pos, team, tier, world) -> {
        for (ServerPlayerEntity player : team) {
            if (player.getPos().isInRange(pos, 20)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 50, tier - 1, false, false));
            }
        }
    }));

    private final TickFunction tickFunction;
    private final TickFunction slowTickFunction;

    public SimpleTickFunction(String id, ItemStack icon, TickFunction tickFunction, TickFunction slowTickFunction) {
        super(id, icon);
        this.tickFunction = tickFunction;
        this.slowTickFunction = slowTickFunction;
    }

    @Override
    public void tick(Vec3d pos, List<ServerPlayerEntity> team, int tier, World world) {
        this.tickFunction.tick(pos, team, tier, world);
    }

    @Override
    public void slowTick(Vec3d pos, List<ServerPlayerEntity> team, int tier, World world) {
        this.slowTickFunction.tick(pos, team, tier, world);
    }
}
