package com.soc.items;

import com.soc.SocWars;
import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.OnHitArmour;
import com.soc.util.DamageTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static com.soc.lib.SocWarsLib.damageSource;
import static com.soc.lib.SocWarsLib.getComponentFromSettingsBuilder;

public class GlassArmour2 extends ArmourItem implements OnHitArmour {
    private static final RegistryKey<EquipmentAsset> GLASS_MODEL_KEY = ArmourItem.register("model");

    public GlassArmour2(Settings settings, EquipmentSlot slot, int armour) {
        super(settings.attributeModifiers(AttributeModifiersComponent.builder().add(EntityAttributes.MAX_HEALTH, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "glass_armour." + slot.getName()), -1d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ARMOR).build()), slot, armour, GLASS_MODEL_KEY);}

    public static void initialise() {
        //addItemToGroupsAndBaseItemGroup(GLASS_HELMET, ItemGroups.COMBAT);
        //addItemToGroupsAndBaseItemGroup(GLASS_CHESTPLATE, ItemGroups.COMBAT);
        //addItemToGroupsAndBaseItemGroup(GLASS_LEGGINGS, ItemGroups.COMBAT);
        //addItemToGroupsAndBaseItemGroup(GLASS_BOOTS, ItemGroups.COMBAT);
    }

    public static final Item GLASS_HELMET = ModItems.register("glass_helmet2", settings -> new GlassArmour2(settings, EquipmentSlot.HEAD, 1), new Settings().maxDamage(325).rarity(Rarity.RARE));
    public static final Item GLASS_CHESTPLATE = ModItems.register("glass_chestplate2", settings -> new GlassArmour2(settings, EquipmentSlot.CHEST, 7), new Settings().maxDamage(400).rarity(Rarity.RARE));
    public static final Item GLASS_LEGGINGS = ModItems.register("glass_leggings2", settings -> new GlassArmour2(settings, EquipmentSlot.LEGS, 5), new Settings().maxDamage(375).rarity(Rarity.RARE));
    public static final Item GLASS_BOOTS = ModItems.register("glass_boots2", settings -> new GlassArmour2(settings, EquipmentSlot.FEET, 3), new Settings().maxDamage(325).rarity(Rarity.RARE));

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
    }

    @Override
    public boolean onHit(ItemStack stack, LivingEntity wearer, World world, DamageSource source) {
        if (world instanceof ServerWorld serverWorld && !source.isOf(DamageTypes.GLASS_ARMOUR)) {
            final DamageSource glassSource = damageSource(serverWorld, DamageTypes.GLASS_ARMOUR, wearer);
            final float baseAmount = world.random.nextFloat();

            if (source.getSource() != null) source.getSource().damage(serverWorld, glassSource, baseAmount * 0.25f + 1f);
        }

        return true;
    }
}
