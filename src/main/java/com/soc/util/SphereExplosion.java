package com.soc.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import static com.soc.lib.SocWarsLib.iterateInSphere;

public class SphereExplosion {
    private SphereExplosion() {}

    public static void explode(World world, BlockPos centre, float explosionRadius, ExplosionBehavior behaviour) {
        boolean damage = world instanceof ServerWorld serverWorld && serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES);

        iterateInSphere(centre, explosionRadius, 1f, pos -> {
            BlockState currentState = world.getBlockState(pos);

            if (!centre.isWithinDistance(pos, explosionRadius - Random.RANDOM.nextFloat())) return;
            if (currentState.isIn(BlockTags.IMMUNE)) return;

            if (currentState == Blocks.WATER.getDefaultState()) trySpawnSteam(world, pos);

            if (damage) world.setBlockState(pos, Blocks.AIR.getDefaultState());
        });

        world.createExplosion(null, Explosion.createDamageSource(world, null), behaviour, centre.getX(), centre.getY() - 2, centre.getZ(), (float)Math.sqrt(explosionRadius), false, World.ExplosionSourceType.TNT);
    }

    private static void trySpawnSteam(World world, float x, float y, float z) {
        float random = world.random.nextFloat();
        if (random < 0.6f) return;
        world.addParticleClient(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0f, random - 0.5f, 0f);
    }
    private static void trySpawnSteam(World world, Vec3i pos) {
        trySpawnSteam(world, pos.getX(), pos.getY(), pos.getZ());
    }
}
