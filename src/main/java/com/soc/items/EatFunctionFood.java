package com.soc.items;

import com.soc.items.util.FinishUsingFunction;
import com.soc.items.util.ModItems;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import static com.soc.items.util.ModItems.addItemToGroups;
import static com.soc.lib.SocWarsLib.SQRT2;
import static com.soc.lib.SocWarsLib.scaleEntity;

public class EatFunctionFood extends Item {
    public static final int CHORUS_SALAD_TRIES = 50;

    private final FinishUsingFunction finishUsingFunction;

    public EatFunctionFood(final Item.Settings settings, final FinishUsingFunction finishUsingFunction, FoodComponent foodComponent) {
        super(settings.food(foodComponent));
        this.finishUsingFunction = finishUsingFunction;
    }
    public EatFunctionFood(final Item.Settings settings, final FinishUsingFunction finishUsingFunction) {
        this(settings, finishUsingFunction, new FoodComponent(0, 1, false));
    }

    public static void initialise() {
        addItemToGroups(SHRINKING_PILLS, ItemGroups.FOOD_AND_DRINK);
        addItemToGroups(BIGGENING_PILLS, ItemGroups.FOOD_AND_DRINK);
        addItemToGroups(CHORUS_FRUIT_SALAD, ItemGroups.FOOD_AND_DRINK);
    }

    public static final Item SHRINKING_PILLS = ModItems.register("shrinking_pills", settings -> new EatFunctionFood(settings, (stack, world, user) -> {
                scaleEntity(user, SQRT2 * 0.5f);
                return null;
    }), new Settings()
            .rarity(Rarity.UNCOMMON));
    public static final Item BIGGENING_PILLS = ModItems.register("biggening_pills", settings -> new EatFunctionFood(settings, (stack, world, user) -> {
                scaleEntity(user, SQRT2);
                return null;
    }), new Settings()
            .rarity(Rarity.UNCOMMON));
    public static final Item CHORUS_FRUIT_SALAD = ModItems.register("chorus_fruit_salad", settings -> new EatFunctionFood(settings, (stack, world, user) -> {
        if (world.isClient) return null;

        for (int i = 0; i < CHORUS_SALAD_TRIES; i++) {
            final int candidateX = (int)user.getX() + world.random.nextBetween(-50, 50);
            final int candidateZ = (int)user.getZ() + world.random.nextBetween(-50, 50);

            final int height = world.getTopY(Heightmap.Type.WORLD_SURFACE, candidateX, candidateZ);

            if (Math.abs(height - user.getY()) > 10) continue;
            final float dX = candidateX - (float)user.getX();
            final float dZ = candidateZ - (float)user.getZ();
            if (dX * dX + dZ * dZ < 15 * 15) continue; //Make sure the user teleports at least 15 blocks away

            user.requestTeleport(candidateX, height, candidateZ);

            break;
        }

        return null;
    }, new FoodComponent(6, 4, true)), new Settings()
            .useCooldown(5f)
            .rarity(Rarity.RARE));

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.finishUsingFunction.finishUsing(stack, world, user);
        return super.finishUsing(stack, world, user);
    }
}
