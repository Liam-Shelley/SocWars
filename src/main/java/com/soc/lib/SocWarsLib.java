package com.soc.lib;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.soc.SocWars;
import com.soc.mixin.MostRecentDamage;
import com.soc.util.Random;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class SocWarsLib {
    public static final Identifier SCALE_MODIFIER_ID = Identifier.of(SocWars.MOD_ID, "scale");
    public static final float SQRT2 = (float)Math.sqrt(2d);
    public static final float MAX_SCALE_FACTOR = 4f;
    public static final byte BLOCKPOS_NBT_TYPE = 101;

    public static <T, U> ImmutableMap<T, U> mapFromCollections(Collection<T> t1, Collection<U> t2) {
        ImmutableMap.Builder<T, U> builder = ImmutableMap.builder();

        Iterator<T> t1Iterator = t1.iterator();
        Iterator<U> t2Iterator = t2.iterator();

        while (t1Iterator.hasNext() && t2Iterator.hasNext()) {
            builder.put(t1Iterator.next(), t2Iterator.next());
        }

        return builder.build();
    }

    public static <T, U> ImmutableMultimap<T, U> multimapFromCollections(Collection<T> t1, Collection<U> t2) {
        ImmutableMultimap.Builder<T, U> builder = ImmutableMultimap.builder();

        Iterator<T> t1Iterator = t1.iterator();
        Iterator<U> t2Iterator = t2.iterator();

        while (t1Iterator.hasNext() && t2Iterator.hasNext()) {
            builder.put(t1Iterator.next(), t2Iterator.next());
        }

        return builder.build();
    }

    public static Optional<Set<BlockPos>> getBlockPosSet(NbtCompound compound, String key) {
        Optional<long[]> value = compound.getLongArray(key);
        return value.map(longs -> Arrays.stream(longs).mapToObj(BlockPos::fromLong).collect(Collectors.toSet()));
    }

    public static void putBlockPosCollection(NbtCompound compound, String key, Collection<BlockPos> blockPosCollection) {
        compound.putLongArray(key, blockPosCollection.stream().mapToLong(BlockPos::asLong).toArray());
    }

    public static Formatting formattingColourFromDye(DyeColor colour) {
        return switch (colour) {
            case WHITE -> Formatting.WHITE;
            case ORANGE -> Formatting.GOLD;
            case MAGENTA, PINK -> Formatting.LIGHT_PURPLE;
            case LIGHT_BLUE -> Formatting.BLUE;
            case YELLOW -> Formatting.YELLOW;
            case LIME -> Formatting.GREEN;
            case GRAY -> Formatting.DARK_GRAY;
            case LIGHT_GRAY -> Formatting.GRAY;
            case CYAN -> Formatting.AQUA;
            case PURPLE -> Formatting.DARK_PURPLE;
            case BLUE -> Formatting.DARK_BLUE;
            case BROWN -> Formatting.DARK_RED;
            case GREEN -> Formatting.DARK_GREEN;
            case RED -> Formatting.RED;
            case BLACK -> Formatting.BLACK;
        };
    }

    public static DyeColor dyeColourFromOrdinal(int ordinal) {
        final DyeColor[] values = DyeColor.values();
        return values[ordinal < values.length ? ordinal : 0];
    }

    public static <T, U> List<T> collectionPairToLeftList(Collection<Pair<T, U>> collection) {
        return collection.stream().map(Pair::getLeft).toList();
    }

    public static <T, U> List<U> collectionPairToRightList(Collection<Pair<T, U>> collection) {
        return collection.stream().map(Pair::getRight).toList();
    }

    public static void scaleEntity(LivingEntity entity, float scale) {
        final EntityAttributeInstance scaleInstance = entity.getAttributeInstance(EntityAttributes.SCALE);

        scaleInstance.overwritePersistentModifier(new EntityAttributeModifier(
                SCALE_MODIFIER_ID,
                Math.clamp(MAX_SCALE_FACTOR - 1, (1 - MAX_SCALE_FACTOR) / MAX_SCALE_FACTOR, scaleInstance.getModifier(SCALE_MODIFIER_ID) == null ? scale - 1f : (scaleInstance.getModifier(SCALE_MODIFIER_ID).value() + (scale - 1f) / scale) * scale),
                EntityAttributeModifier.Operation.ADD_VALUE)
        );

        if (Math.abs(scaleInstance.getModifier(SCALE_MODIFIER_ID).value()) < 1e-5) scaleInstance.removeModifier(SCALE_MODIFIER_ID);
    }

    public static BlockPos[] findAdjacentBlocksFromViewAngle(BlockPos pos, double angle) {
        return Arrays.stream(new double[] {-Math.PI / 4d, 0, Math.PI / 4d}).mapToObj(offset -> {
            final double a = offset + ((Math.PI / 4d) * Math.round(angle * 4d / Math.PI));
            final double radius = polarSquareRadius(a);

            final int x = (int)Math.round((Math.cos(a) * 100d) / 100d * radius);
            final int z = (int)Math.round((Math.sin(a) * 100d) / 100d * radius);

            return pos.add(x, 0, z);
        }).toArray(BlockPos[]::new);
    }

    public static double polarSquareRadius(double angle) {
        final double pi2 = (Math.PI / 2d);
        final double pi4 = (Math.PI / 4d);

        final double angleMinusPi4 = angle - pi4;
        final double angleModPi2 = Math.abs(angleMinusPi4 % pi2);
        final double finalAngle = angleModPi2 - pi4;

        final double cosAngle = Math.cos(finalAngle);
        final double result = 1d / cosAngle;

        return result;
    }

    public static void iterateInSphere(Vec3i centre, float radius, float randomRadiusFactor, Consumer<BlockPos> function) {
        final int intRadius = (int)Math.ceil(radius);
        final Predicate<BlockPos> sphereCheckFunction = randomRadiusFactor > 10e-5 ? pos -> centre.isWithinDistance(pos, radius - Random.RANDOM.nextFloat()) : pos -> centre.isWithinDistance(pos, radius);

        iterateInCube(centre, intRadius, pos -> {
            if (sphereCheckFunction.test(pos)) function.accept(pos);
        });
    }

    public static void iterateInCube(Vec3i centre, int radius, Consumer<BlockPos> function) {
        final Vec3i cornerSize = new Vec3i(radius, radius, radius);

        final Vec3i minPos = centre.subtract(cornerSize);
        final Vec3i maxPos = centre.add(cornerSize).add(1, 1, 1);

        iterateInCube(minPos, maxPos, function);
    }

    public static void iterateInPlane(Vec3i centre, int radius, Consumer<BlockPos> function) {
        final Vec3i cornerSize = new Vec3i(radius, 0, radius);

        final Vec3i minPos = centre.subtract(cornerSize);
        final Vec3i maxPos = centre.add(cornerSize).add(1, 1, 1);

        iterateInCube(minPos, maxPos, function);
    }

    public static void iterateInCube(Vec3i minPos, Vec3i maxPos, Consumer<BlockPos> function) {
        for (int x = minPos.getX(); x < maxPos.getX(); x++) {
            for (int y = minPos.getY(); y < maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z < maxPos.getZ(); z++) {
                    function.accept(new BlockPos(x, y, z));
                }
            }
        }
    }

    public static boolean isBlockHidden(World world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (world.isAir(pos.offset(direction))) return false;
        }
        return true;
    }

    public static float getHoldTimeSeconds(int progress) {
        return Math.min(1, -progress / 20f);
    }

    public static boolean hasInfinity(ItemStack stack) {
        return stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).getEnchantments().stream().anyMatch(entry -> entry.getKey().isPresent() && entry.getKey().get() == Enchantments.INFINITY);
    }

    public static boolean randomTeleport(World world, Entity user, int attempts, int range, float minRange) {
        Optional<Vec3d> destination = findRandomOpenPos(world, user.getPos(), attempts, range, minRange);

        destination.ifPresent(pos -> user.requestTeleport(pos.x, pos.y, pos.z));

        return destination.isPresent();
    }

    public static Optional<Vec3d> findRandomOpenPos(World world, Position origin, int attempts, int range, float minRange) {
        for (int i = 0; i < attempts; i++) {
            final int candidateX = (int)origin.getX() + world.random.nextBetween(-range, range);
            final int candidateZ = (int)origin.getZ() + world.random.nextBetween(-range, range);

            final int height = world.getTopY(Heightmap.Type.MOTION_BLOCKING, candidateX, candidateZ);
            if (Math.abs(height - origin.getY()) > 10) continue;

            final float dX = candidateX - (float)origin.getX();
            final float dZ = candidateZ - (float)origin.getZ();
            if (dX * dX + dZ * dZ < minRange * minRange) continue;

            return Optional.of(new Vec3d(candidateX, height, candidateZ));
        }
        return Optional.empty();
    }

    public static DamageSource damageSource(World world, RegistryKey<DamageType> damageType, Entity attacker) {
        return new DamageSource(
                world.getRegistryManager()
                        .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                        .getEntry(damageType.getValue()).get(),
                attacker
        );
    }

    public static DamageSource damageSource(World world, RegistryKey<DamageType> damageType) {
        return damageSource(world, damageType, null);
    }

    public static void copyTeam(World world, LivingEntity assignee, LivingEntity source) {
        Scoreboard scoreboard = world.getScoreboard();
        if (source.getScoreboardTeam() == null && source instanceof PlayerEntity player) {
            player.sendMessage(Text.literal("You are not assigned to a team, go yell at Liam"), false);
        } else {
            scoreboard.addScoreHolderToTeam(assignee.getNameForScoreboard(), source.getScoreboardTeam());
        }
    }

    public static LivingEntity randomHostileMob(ServerWorld world, Vec3d pos) {
        int index = world.random.nextBetween(0, 20);
        if (index == 20 && world.random.nextFloat() < 0.4f) index = world.random.nextBetween(0, 19); //Hacky way to make the warden a 2% chance

        final LivingEntity mob = switch (index) {
            case 0 -> new ZombieEntity(EntityType.ZOMBIE, world);
            case 1 -> new SkeletonEntity(EntityType.SKELETON, world);
            case 2 -> new CreeperEntity(EntityType.CREEPER, world);
            case 3 -> new SpiderEntity(EntityType.SPIDER, world);
            case 4 -> new GhastEntity(EntityType.GHAST, world);
            case 5 -> new PiglinBruteEntity(EntityType.PIGLIN_BRUTE, world);
            case 6 -> new EndermanEntity(EntityType.ENDERMAN, world);
            case 7 -> new GuardianEntity(EntityType.GUARDIAN, world);
            case 8 -> new PhantomEntity(EntityType.PHANTOM, world);
            case 9 -> {
                final CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
                creeper.onStruckByLightning(world, null);
                yield creeper;
            }
            case 10 -> new WitherSkeletonEntity(EntityType.WITHER_SKELETON, world);
            case 11 -> new BlazeEntity(EntityType.BLAZE, world);
            case 12 -> new EvokerEntity(EntityType.EVOKER, world);
            case 13 -> new IllusionerEntity(EntityType.ILLUSIONER, world);
            case 14 -> new ElderGuardianEntity(EntityType.ELDER_GUARDIAN, world);
            case 15 -> new GiantEntity(EntityType.GIANT, world);
            case 16 -> new EndermiteEntity(EntityType.ENDERMITE, world);
            case 17 -> new SilverfishEntity(EntityType.SILVERFISH, world);
            case 18 -> new HuskEntity(EntityType.HUSK, world);
            case 19 -> new StrayEntity(EntityType.STRAY, world);
            case 20 -> new WardenEntity(EntityType.WARDEN, world);
            default -> throw new IllegalStateException("RNG set up incorrectly, leading to an invalid switch case");
        };

        mob.setPosition(pos);

        return mob;
    }

    public static void swapPositions(Entity a, Entity b) {
        final Vec3d aPos = new Vec3d(a.getPos().toVector3f());
        final Vec3d bPos = new Vec3d(b.getPos().toVector3f());

        if (a instanceof PlayerEntity player) {
            player.requestTeleport(bPos.getX(), bPos.getY(), bPos.getZ());
        } else {
            a.setPosition(bPos);
        }

        if (b instanceof PlayerEntity player) {
            player.requestTeleport(aPos.getX(), aPos.getY(), aPos.getZ());
        } else {
            b.setPosition(aPos);
        }
    }

    public static void rainPositions(World world, Vec3d origin, int num, float radius, float minHeight, float maxHeight, Consumer<Vec3d> function) {
        final float heightRange = maxHeight - minHeight;

        for (int i = 0; i < num; i++) {
            final double range = radius * Math.sqrt(world.random.nextFloat());
            final double angle = world.random.nextFloat() * 2d * Math.PI;

            final Vec3d pos = new Vec3d(Math.cos(angle) * range, minHeight + heightRange * world.random.nextFloat(), Math.sin(angle) * range).add(origin);
            function.accept(pos);
        }
    }

    public static Optional<Hand> handFromEquipmentSlot(@Nullable EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> Optional.of(Hand.MAIN_HAND);
            case OFFHAND -> Optional.of(Hand.OFF_HAND);
            case null, default -> Optional.empty();
        };
    }

    public static boolean EquipmentSlotIsHand(@Nullable EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND;
    }

    public static <T> T getIndexWithFallback(T[] colours, int index, T fallback) {
        return colours.length > index ? colours[index] : fallback;
    }

    public static int colourFromList(Integer[] colours, int index) {
        return getIndexWithFallback(colours, index, 0xffffffff);
    }

    @SafeVarargs
    public static Text getTimeFromTicksDynColours(float time, boolean includeTicks, UnaryOperator<Integer>... colours) {
        final int minutes = (int)(time / 60);
        final int seconds = (int)time % 60;

        final MutableText minutesText = Text.literal(StringUtils.leftPad(String.valueOf(minutes), 2, '0')).withColor(getIndexWithFallback(colours, 0, a -> 0xffffffff).apply(minutes));
        final Text secondsText = Text.literal(":" + StringUtils.leftPad(String.valueOf(seconds), 2, '0')).withColor(getIndexWithFallback(colours, 1, a -> 0xffffffff).apply(seconds));

        if (!includeTicks) {
            return minutesText.append(secondsText);
        } else {
            final int ticks = (int)(time * 20) % 20;
            final Text ticksText = Text.literal("+" + StringUtils.leftPad(String.valueOf(ticks), 2, '0')).withColor(getIndexWithFallback(colours, 2, a -> 0xffffffff).apply(ticks));
            return minutesText.append(secondsText).append(ticksText);
        }
    }

    public static Text getTimeFromTicks(float time, boolean includeTicks, int... colours) {
        return getTimeFromTicksDynColours(time, includeTicks, Arrays.stream(colours).mapToObj(colour -> (UnaryOperator<Integer>)(a -> colour)).toArray(UnaryOperator[]::new));
    }

    public static Optional<PlayerEntity> getPlayerAttacker(PlayerEntity player) {
        for (DamageRecord record : ((MostRecentDamage)player.getDamageTracker()).getRecentDamage()) {
            final Entity source = record.damageSource().getSource();
            if (source != null && source.getType() == EntityType.PLAYER) {
                return Optional.of((PlayerEntity)source);
            }
        }
        return Optional.empty();
    }
}
