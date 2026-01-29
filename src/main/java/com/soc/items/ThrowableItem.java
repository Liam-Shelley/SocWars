package com.soc.items;

import com.soc.entities.BWFireballEntity;
import com.soc.entities.EnderBeamEntity;
import com.soc.entities.util.ModEntities;
import com.soc.items.util.ModItems;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.awt.*;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.damageSource;

public class ThrowableItem extends Item {
    public static World WORLD;

    public enum ThrowableType {
        FIREBALL,
        SNAIL,
        DRAGON,
        TNT,
        ENDER,
    }

    private final ThrowableType fireballType;

    public ThrowableItem(Settings settings, ThrowableType fireballType) {
        super(settings);
        this.fireballType = fireballType;
    }

    public static void initialise() {
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(FIREBALL, ItemGroups.COMBAT);
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(DRAGON_FIREBALL, ItemGroups.COMBAT);
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(SNAIL_FIREBALL, ItemGroups.COMBAT);
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(THROWABLE_TNT, ItemGroups.COMBAT);
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(ENDER_BEAM, ItemGroups.COMBAT);

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.LOAD.register((a, b) -> WORLD = b);
    }

    public static final Item FIREBALL = ModItems.register("fireball", settings -> new ThrowableItem(settings, ThrowableType.FIREBALL), new Settings().useCooldown(0.75f));
    public static final Item DRAGON_FIREBALL = ModItems.register("dragon_fireball", settings -> new ThrowableItem(settings, ThrowableType.DRAGON), new Settings().useCooldown(0.75f).rarity(Rarity.UNCOMMON));
    public static final Item SNAIL_FIREBALL = ModItems.register("snail_fireball", settings -> new ThrowableItem(settings, ThrowableType.SNAIL), new Settings().useCooldown(0.75f).rarity(Rarity.EPIC));
    public static final Item THROWABLE_TNT = ModItems.register("throwable_tnt", settings -> new ThrowableItem(settings, ThrowableType.TNT), new Settings().useCooldown(0.75f));
    public static final Item ENDER_BEAM = ModItems.register("ender_beam", settings -> new ThrowableItem(settings, ThrowableType.ENDER), new Settings().useCooldown(0.75f));

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        final ItemStack itemStack = user.getStackInHand(hand);

        if (world instanceof ServerWorld serverWorld) {
            switch (this.fireballType) {
                case FIREBALL -> spawnEntityWithVelocity(new BWFireballEntity(world, user, Vec3d.ZERO, 4), serverWorld, user, 1.75f);
                case SNAIL -> spawnEntityWithVelocity(new BWFireballEntity(world, user, Vec3d.ZERO, 20, ThrowableItem::snailExplosion), serverWorld, user, 0.2f);
                case DRAGON -> spawnEntityWithVelocity(new DragonFireballEntity(world, user, Vec3d.ZERO), serverWorld, user, 1.5f);
                case TNT -> {
                    final TntEntity tnt = spawnEntityWithVelocity(new TntEntity(EntityType.TNT, world), serverWorld, user, 0.6f);
                    tnt.setFuse(40);
                }
                case ENDER -> spawnEntityWithVelocity(new EnderBeamEntity(ModEntities.ENDER_BEAM, world), serverWorld, user, 1f);
            }
        }

        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    private static void snailExplosion(Entity self, ServerWorld serverWorld, Vec3d pos, float explosionPower, Entity owner) {
        serverWorld.createExplosion(self, damageSource(serverWorld, DamageTypes.EXPLOSION, owner), new ExplosionBehavior() {
            @Override
            public float calculateDamage(Explosion explosion, Entity entity, float amount) {
                return super.calculateDamage(explosion, entity, amount) * 0.09f;
            }
        }, pos.x, pos.y, pos.z, explosionPower, true, World.ExplosionSourceType.BLOCK);
    }

    public static <T extends Entity> T spawnEntityWithVelocity(T entity, ServerWorld world, LivingEntity user, float speed) {
        entity.setPosition(user.getEyePos());
        entity.setVelocity(user.getRotationVector().multiply(speed));

        world.spawnEntity(entity);

        return entity;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (this.fireballType) {
            case SNAIL -> textConsumer.accept(Text.translatable("tooltip.snail_fireball").withColor(0xe6e475));
            case DRAGON -> textConsumer.accept(Text.translatable("tooltip.dragon_fireball").withColor(Color.HSBtoRGB(WORLD == null ? 0f : WORLD.getTime() / 50f, 1f, 1f)));
        }
    }
}
