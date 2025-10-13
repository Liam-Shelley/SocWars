package com.soc.items;

import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnHitArmour;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import static com.soc.lib.SocWarsLib.copyTeam;
import static com.soc.lib.SocWarsLib.randomHostileMob;

public class SummonersGarb extends ArmourItem implements OnHitArmour {
    private static final RegistryKey<EquipmentAsset> SUMMONERS_GARB_MODEL_KEY = ArmourItem.register("summoners_garb");

    public SummonersGarb(Settings settings, EquipmentSlot slot, int armour) {
        super(settings, slot, armour, SUMMONERS_GARB_MODEL_KEY);
    }

    public static void initialise() {
        ModItems.addItemToGroups(SUMMONERS_GARB, ItemGroups.COMBAT);
    }

    public static final Item SUMMONERS_GARB = ModItems.register("summoners_garb", settings -> new SummonersGarb(settings, EquipmentSlot.CHEST, 6), new Settings()
            .rarity(Rarity.RARE)
            .maxDamage(400)
    );


    @Override
    public boolean onHit(ItemStack stack, LivingEntity wearer, World world) {
        if (world instanceof ServerWorld serverWorld && world.random.nextFloat() > 0.5f) {
            final LivingEntity mob = randomHostileMob(serverWorld, wearer.getPos());
            copyTeam(world, mob, wearer);

            world.spawnEntity(mob);
        }
        return true;
    }
}
