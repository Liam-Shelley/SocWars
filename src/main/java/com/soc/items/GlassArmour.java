package com.soc.items;

import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnHitArmour;
import com.soc.util.DamageTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.damageSource;

public class GlassArmour extends ArmourItem implements OnHitArmour {
    private static final RegistryKey<EquipmentAsset> GLASS_MODEL_KEY = ArmourItem.registerEquipmentAsset("glass");

    public GlassArmour(Settings settings, EquipmentSlot slot, int armour) {
        super(settings, slot, armour, GLASS_MODEL_KEY);
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(GLASS_HELMET, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(GLASS_CHESTPLATE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(GLASS_LEGGINGS, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(GLASS_BOOTS, ItemGroups.COMBAT);

        GlassArmour2.initialise();
    }

    public static final Item GLASS_HELMET = ModItems.register("glass_helmet", settings -> new GlassArmour(settings, EquipmentSlot.HEAD, 1), new Settings().maxDamage(325).rarity(Rarity.RARE));
    public static final Item GLASS_CHESTPLATE = ModItems.register("glass_chestplate", settings -> new GlassArmour(settings, EquipmentSlot.CHEST, 7), new Settings().maxDamage(400).rarity(Rarity.RARE));
    public static final Item GLASS_LEGGINGS = ModItems.register("glass_leggings", settings -> new GlassArmour(settings, EquipmentSlot.LEGS, 5), new Settings().maxDamage(375).rarity(Rarity.RARE));
    public static final Item GLASS_BOOTS = ModItems.register("glass_boots", settings -> new GlassArmour(settings, EquipmentSlot.FEET, 3), new Settings().maxDamage(325).rarity(Rarity.RARE));

    @Override
    public boolean onHit(ItemStack stack, LivingEntity wearer, World world, DamageSource source) {
        if (world instanceof ServerWorld serverWorld && !source.isOf(DamageTypes.GLASS_ARMOUR)) {
            final DamageSource glassSource = damageSource(serverWorld, DamageTypes.GLASS_ARMOUR, wearer);
            final float baseAmount = world.random.nextFloat();

            if (source.getSource() != null) source.getSource().damage(serverWorld, glassSource, baseAmount * 0.25f + 1f);

            wearer.damage(serverWorld, glassSource, baseAmount * 0.2f + 0.5f);
        }

        return true;
    }
}
