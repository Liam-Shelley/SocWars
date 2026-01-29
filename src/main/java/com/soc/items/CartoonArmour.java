package com.soc.items;

import com.soc.entities.BWFireballEntity;
import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnHitArmour;
import com.soc.util.BlockTags;
import com.soc.util.SphereExplosion;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

import static com.soc.items.ThrowableItem.spawnEntityWithVelocity;
import static com.soc.lib.SocWarsLib.*;

public class CartoonArmour extends ArmourItem implements OnHitArmour {
    private final OnHitArmour onHitEffect;

    private static final RegistryKey<EquipmentAsset> CARTOON_MODEL_KEY = ArmourItem.register("cartoon");

    public CartoonArmour(Settings settings, EquipmentSlot slot, int armour, final OnHitArmour onHitEffect) {
        super(settings, slot, armour, CARTOON_MODEL_KEY);
        this.onHitEffect = onHitEffect;
    }

    public static void initialise() {
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(CARTOON_HELMET, ItemGroups.COMBAT);
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(CARTOON_CHESTPLATE, ItemGroups.COMBAT);
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(CARTOON_LEGGINGS, ItemGroups.COMBAT);
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(CARTOON_BOOTS, ItemGroups.COMBAT);
    }

    public static final Item CARTOON_HELMET = ModItems.register("cartoon_helmet", settings -> new CartoonArmour(settings, EquipmentSlot.HEAD, 4, (stack, wearer, world) -> {
            if (world.isClient) return true;
            final int random = world.random.nextBetween(1, 8);
            switch (random) {
                case 1 -> wearer.requestTeleport(wearer.getX(), wearer.getY() + 3d, wearer.getZ());
                case 2 -> wearer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 0, false, false));
                case 3 -> world.playSound(null, wearer.getX(), wearer.getY(), wearer.getZ(), SoundEvents.ENTITY_ITEM_BREAK.value(), SoundCategory.PLAYERS);
                case 4 -> {
                    final PlayerEntity other = world.getClosestPlayer(wearer.getX(), wearer.getY(), wearer.getZ(), 100f, entity -> entity != wearer);
                    if (other != null) {
                        swapPositions(wearer, other);
                    } else {
                        randomTeleport(world, wearer, 10, 20, 5f);
                    }
                }
            }

            return true;
    }), new Settings().maxDamage(325).rarity(Rarity.RARE));
    public static final Item CARTOON_CHESTPLATE = ModItems.register("cartoon_chestplate", settings -> new CartoonArmour(settings, EquipmentSlot.CHEST, 8, (stack, wearer, world) -> {
            if (world.isClient) return true;
            final int random = world.random.nextBetween(1, 8);
            switch (random) {
                case 1 -> { return false; }
                case 2 -> SphereExplosion.explode(world, wearer.getPos(), 4.5f, 0.8f, 0.75f, wearer);
                case 3 -> {
                    iterateInPlane(wearer.getBlockPos(), 1, pos -> world.setBlockState(pos, Blocks.AIR.getDefaultState()));
                    iterateInPlane(wearer.getBlockPos().down(), 1, pos -> world.setBlockState(pos, Blocks.SLIME_BLOCK.getDefaultState()));
                }
                case 4 -> wearer.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2 * 20, 9, false, false));
            }

            return true;
    }), new Settings().maxDamage(400).rarity(Rarity.RARE));
    public static final Item CARTOON_LEGGINGS = ModItems.register("cartoon_leggings", settings -> new CartoonArmour(settings, EquipmentSlot.LEGS, 6, (stack, wearer, world) -> {
            if (world.isClient) return true;
            final int random = world.random.nextBetween(1, 8);
            switch (random) {
                case 1 -> {
                    for (Hand hand : Hand.values()) {
                        wearer.dropItem(wearer.getStackInHand(hand), false, false);
                        wearer.setStackInHand(hand, ItemStack.EMPTY);
                    }
                }
                case 2 -> spawnEntityWithVelocity(new BWFireballEntity(world, wearer, Vec3d.ZERO, 3), (ServerWorld)world, wearer, 0.8f);
                case 3 -> rainPositions(world, wearer.getPos(), 150, 22f, 10f, 250f, pos -> {
                    final CodEntity cod = new CodEntity(EntityType.COD, world);

                    cod.setPosition(pos);
                    world.spawnEntity(cod);
                });
                case 4 -> {
                    final PlayerEntity player = world.getClosestPlayer(wearer.getX(), wearer.getY(), wearer.getZ(), 100f, entity -> entity != wearer);
                    if (player != null) player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2 * 20, 9, false, false));
                }
            }

            return true;
    }), new Settings().maxDamage(375).rarity(Rarity.RARE));
    public static final Item CARTOON_BOOTS = ModItems.register("cartoon_boots", settings -> new CartoonArmour(settings, EquipmentSlot.FEET, 4, (stack, wearer, world) -> {
            if (world.isClient) return true;
            final int random = world.random.nextBetween(1, 8);
            switch (random) {
                case 1 -> {
                    final RavagerEntity ravager = new RavagerEntity(EntityType.RAVAGER, world);
                    findRandomOpenPos(world, wearer.getPos(), 50, 20, 5f).ifPresentOrElse(ravager::setPosition, () -> ravager.setPosition(wearer.getPos()));
                    copyTeam(world, ravager, wearer);
                    world.spawnEntity(ravager);
                }
                case 2 -> wearer.giveOrDropStack(Items.PIGLIN_HEAD.getDefaultStack());
                case 3 -> {
                    final BlockPos centre = wearer.getBlockPos();
                    iterateInSphere(centre, 6f, 0f, pos -> {
                        if (centre.isWithinDistance(pos, 5f) || world.getBlockState(pos).isIn(BlockTags.IMMUNE)) return;
                        world.setBlockState(pos, Blocks.RESIN_BLOCK.getDefaultState());
                    });
                }
                case 4 -> {
                    final PlayerEntity player = world.getClosestPlayer(wearer.getX(), wearer.getY(), wearer.getZ(), 100f, entity -> entity != wearer);
                    if (player != null) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 10 * 20, 3, false, true));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 10 * 20, 1, false, true));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 10 * 20, 0, false, true));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10 * 20, 0, false, true));
                    }
                }
            }

            return true;
    }), new Settings().maxDamage(325).rarity(Rarity.RARE));

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
    }

    @Override
    public boolean onHit(ItemStack stack, LivingEntity wearer, World world) {
        return this.onHitEffect.onHit(stack, wearer, world);
    }
}
