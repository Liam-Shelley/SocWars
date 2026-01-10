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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
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

import java.awt.*;
import java.util.function.Consumer;

import static com.soc.items.util.ModItems.addItemToGroups;

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
        addItemToGroups(FIREBALL, ItemGroups.COMBAT);
        addItemToGroups(DRAGON_FIREBALL, ItemGroups.COMBAT);
        addItemToGroups(SNAIL_FIREBALL, ItemGroups.COMBAT);
        addItemToGroups(THROWABLE_TNT, ItemGroups.COMBAT);
        addItemToGroups(ENDER_BEAM, ItemGroups.COMBAT);

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
                case SNAIL -> spawnEntityWithVelocity(new BWFireballEntity(world, user, Vec3d.ZERO, 10), serverWorld, user, 0.2f);
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

    public static <T extends Entity> T spawnEntityWithVelocity(T entity, ServerWorld world, LivingEntity user, float speed) {
        entity.setPosition(user.getEyePos());
        entity.setVelocity(user.getRotationVector().multiply(speed));

        world.spawnEntity(entity);

        return entity;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        final Text text = switch (this.fireballType) {
            case FIREBALL, TNT, ENDER -> null;
            case SNAIL -> Text.translatable("tooltip.snail_fireball").withColor(0xe6e475);
            case DRAGON -> Text.translatable("tooltip.dragon_fireball").withColor(Color.HSBtoRGB(WORLD == null ? 0f : WORLD.getTime() / 50f, 1f, 1f));
        };

        if (text != null) {
            textConsumer.accept(text);
        }
    }
}
