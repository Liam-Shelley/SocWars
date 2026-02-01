package com.soc.util;

import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.GamesManager;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static com.soc.lib.SocWarsLib.damageSource;
import static com.soc.lib.SocWarsLib.iterateInSphere;

public class SphereExplosion {
    private SphereExplosion() {}

    public static void explode(World world, Vec3d centre, float explosionRadius, float explosionVariance, float damageFactor, float knockbackFactor, boolean blockDamage, @Nullable Entity causingEntity, @Nullable RegistryKey<DamageType> damageType) {
        final Optional<AbstractGameManager<?, ?, ?>> managerOptional = causingEntity == null ? Optional.empty() : GamesManager.getInstance().getGame(causingEntity);

        final Predicate<BlockPos> damage;
        if (!blockDamage) {
            damage = pos -> false;
        } else if (managerOptional.isPresent()) {
            final AbstractGameManager<?, ?, ?> manager = managerOptional.get();
            damage = manager::isBlockUnprotected;
        } else {
            final boolean def = world instanceof ServerWorld serverWorld && serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES);
            damage = pos -> def;
        }

        iterateInSphere(BlockPos.ofFloored(centre), explosionRadius, explosionVariance, pos -> {
                final BlockState currentState = world.getBlockState(pos);

                if (currentState.isIn(BlockTags.IMMUNE)) return;

                if (currentState == Blocks.WATER.getDefaultState()) trySpawnSteam(world, pos);

                if (damage.test(pos)) world.setBlockState(pos, Blocks.AIR.getDefaultState());
        });

        applyDamageAndKnockback(world, centre, explosionRadius, damageFactor, knockbackFactor, damageSource(world, damageType == null ? DamageTypes.SPHERE_EXPLOSION : damageType, causingEntity));
    }

    public static void explode(World world, Vec3d centre, float explosionRadius, float damageFactor, float knockbackFactor, boolean blockDamage, @Nullable Entity causingPlayer, @Nullable RegistryKey<DamageType> damageType) {
        explode(world, centre, explosionRadius, 1.5f, damageFactor, knockbackFactor, blockDamage, causingPlayer, damageType);
    }

    private static void applyDamageAndKnockback(World world, Vec3d centre, float explosionRadius, float damageFactor, float knockbackFactor, DamageSource source) {
        final List<Entity> nearbyEntities = world.getOtherEntities(null, Box.of(centre, explosionRadius * 4f, explosionRadius * 4f, explosionRadius * 4f));
        final Set<PlayerEntity> knockbackAppliedPlayers = new HashSet<>();

        nearbyEntities.forEach(entity -> {
            final Vec3d pos = entity.getPos();
            final float distance = (float)pos.distanceTo(centre);

            if (distance > explosionRadius * 2f) return;

            final float intensity = Math.min(1f / distance, 1.5f) * explosionRadius;

            final Vec3d knockback = pos.subtract(centre.subtract(0d, 0.5d, 0d)).normalize().multiply(intensity * knockbackFactor * 0.25f);
            if (entity instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new ExplosionS2CPacket(centre, Optional.of(knockback), ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE));
                knockbackAppliedPlayers.add(serverPlayer);
            } else {
                entity.addVelocity(knockback);
            }

            if (world instanceof ServerWorld serverWorld) {
                entity.damage(serverWorld, source, intensity * damageFactor);
            }
        });

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getPlayers().forEach(player -> {
               if (player.getPos().isInRange(centre, 250) && !knockbackAppliedPlayers.contains(player)) {
                   player.networkHandler.sendPacket(new ExplosionS2CPacket(centre, Optional.empty(), ParticleTypes.EXPLOSION, SoundEvents.ENTITY_GENERIC_EXPLODE));
               }
            });
        }
    }

    private static void trySpawnSteam(World world, float x, float y, float z) {
        final float random = world.random.nextFloat();
        if (random < 0.6f) return;
        world.addParticleClient(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0f, random - 0.5f, 0f);
    }

    private static void trySpawnSteam(World world, Vec3i pos) {
        trySpawnSteam(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void fireExplosion(World world, BlockPos centre, float radius, float fireChance) {
        iterateInSphere(centre, radius, 0f, pos -> {
                if (world.random.nextFloat() < fireChance && AbstractFireBlock.canPlaceAt(world, pos, Direction.DOWN)) {
                    world.setBlockState(pos, AbstractFireBlock.getState(world, pos));
                }
        });
    }

    public static void fireExplosion(World world, BlockPos centre, float radius) {
        fireExplosion(world, centre, radius, 0.1f);
    }
}
