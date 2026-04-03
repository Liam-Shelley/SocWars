package com.soc.items;

import com.soc.items.util.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class TauntStick extends Item {
    private static final SoundEvent[] TAUNTS = {
            SoundEvents.BLOCK_ANVIL_USE,
            SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER,
            SoundEvents.BLOCK_BELL_USE,
            SoundEvents.ENTITY_FROG_DEATH,
            SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(),
            SoundEvents.ENTITY_VILLAGER_YES,
            SoundEvents.ENTITY_VILLAGER_AMBIENT,
            SoundEvents.ENTITY_CHICKEN_HURT,
            SoundEvents.ENTITY_FOX_SCREECH,
            SoundEvents.ENTITY_COD_FLOP,
            SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
            SoundEvents.ENTITY_GOAT_SCREAMING_AMBIENT,
            SoundEvents.ENTITY_GOAT_SCREAMING_DEATH,
            SoundEvents.ENTITY_GOAT_SCREAMING_PREPARE_RAM
    };

    public TauntStick(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(TAUNT_STICK, ItemGroups.COMBAT);
    }

    public static final Item TAUNT_STICK = ModItems.register("taunt_stick", TauntStick::new, new Settings().rarity(Rarity.UNCOMMON).useCooldown(20 * 20));

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        final SoundEvent soundEvent = TAUNTS[world.random.nextBetween(0, TAUNTS.length - 1)];
        world.playSound(null, user.getBlockPos(), soundEvent, SoundCategory.MASTER);

        return super.use(world, user, hand);
    }
}
