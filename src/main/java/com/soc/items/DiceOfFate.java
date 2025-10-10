package com.soc.items;

import com.soc.items.util.EffectRecord;
import com.soc.items.util.ModItems;
import com.soc.networking.s2c.DiceOfFatePayload;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Arrays;

import static com.soc.items.util.ModItems.addItemToGroups;
import static net.minecraft.item.Items.*;

public class DiceOfFate extends Item {
    public enum Effect implements StringIdentifiable {
        SWORD("sword"),
        ENCHANTED_GAPPLE("enchanted_gapple"),
        EXPLODE("explode"),
        POTION_RING("potion_ring"),
        LAUNCH("launch"),
        DEBUFFS("debuffs");

        public static final PacketCodec<ByteBuf, Effect> PACKET_CODEC = PacketCodecs.indexed(Effect::fromOrdinal, Effect::ordinal);

        private final String name;

        Effect(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }

        public static Effect fromOrdinal(int ordinal) {
            Effect[] values = Effect.values();
            if (ordinal >= values.length || ordinal < 0) throw new IllegalArgumentException("No DiceOfFate.Effect value with ordinal " + ordinal);

            return values[ordinal];
        }
    }

    private static final Item[] RANDOM_RINGS = {
        InvisibilityRing.INVISIBILITY_RING,
        PotionRing.LESSER_SPEED_RING,
        PotionRing.GREATER_SPEED_RING,
        PotionRing.LESSER_JUMP_RING,
        PotionRing.GREATER_JUMP_RING
    };

    private final EffectRecord[] GARBO_EFFECTS = {
            new EffectRecord(StatusEffects.SLOWNESS, 3),
            new EffectRecord(StatusEffects.MINING_FATIGUE, 1),
            new EffectRecord(StatusEffects.NAUSEA, 0),
            new EffectRecord(StatusEffects.BLINDNESS, 0),
            new EffectRecord(StatusEffects.HUNGER, 0),
            new EffectRecord(StatusEffects.WEAKNESS, 3),
            new EffectRecord(StatusEffects.POISON, 1),
            new EffectRecord(StatusEffects.GLOWING, 0),
    };

    public DiceOfFate(Item.Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroups(DICE_OF_FATE);
    }

    public static final Item DICE_OF_FATE = ModItems.register("dice_of_fate", DiceOfFate::new, new Settings()
            .maxCount(1)
            .rarity(Rarity.RARE)
    );

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return ActionResult.SUCCESS;

        final Effect effect = Effect.fromOrdinal(world.random.nextBetween(0, 5));

        final ItemStack stack = user.getStackInHand(hand);
        stack.decrementUnlessCreative(1, user);
        user.sendMessage(Text.translatable("dice_of_fate.roll_result", effect.ordinal() + 1), true); //Make translatable

        switch (effect) {
            case SWORD -> giveItem(user, NETHERITE_SWORD.getDefaultStack(), hand);
            case ENCHANTED_GAPPLE -> giveItem(user, ENCHANTED_GOLDEN_APPLE.getDefaultStack(), hand);
            case EXPLODE -> explode(world, user);
            case POTION_RING -> {
                ItemStack ring = RANDOM_RINGS[world.random.nextBetween(0, RANDOM_RINGS.length - 1)].getDefaultStack();
                giveItem(user, ring, hand);
            }
            case LAUNCH -> ServerPlayNetworking.send((ServerPlayerEntity) user, new DiceOfFatePayload(effect));
            case DEBUFFS -> Arrays.stream(GARBO_EFFECTS).forEach(effectRecord -> user.addStatusEffect(new StatusEffectInstance(effectRecord.effect(), 20 * 20, effectRecord.amplifier(), false, true, true)));
        }

        return ActionResult.FAIL;
    }

    private static void giveItem(PlayerEntity user, ItemStack item, Hand hand) {
        if (user.getStackInHand(hand).isEmpty()) {
            user.setStackInHand(hand, item);
        } else {
            user.giveItemStack(item);
        }
    }

    private static void explode(World world, PlayerEntity user) {
        if (world instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1, 1, false, false, false));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1, 11, false, false, false));
                world.createExplosion(null, Explosion.createDamageSource(world, null), null, user.getX(), user.getY(), user.getZ(), 8.0f, false, World.ExplosionSourceType.TNT);
            }
        }
    }
}
