package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.effects.util.OnEffectRemoved;
import com.soc.items.components.ModItemComponents;
import com.soc.items.components.ArmourSetBonus;
import com.soc.lib.ModEntityAttributes;
import com.soc.util.MutableBoolean;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.ifNotNull;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    @Final
    private AttributeContainer attributes;

    @Redirect(method = "createLivingAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;add(Lnet/minecraft/registry/entry/RegistryEntry;)Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", ordinal = 0))
    private static DefaultAttributeContainer.Builder a(DefaultAttributeContainer.Builder instance, RegistryEntry<EntityAttribute> attribute) {
        instance.add(attribute);
        instance.add(ModEntityAttributes.EXPLOSION_RESISTANCE);
        return instance;
    }

    @Inject(method = "removeStatusEffect", at = @At(value = "RETURN", ordinal = 0))
    private void socwars_onRemoveEffect(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Boolean> cir, @Local StatusEffectInstance statusEffectInstance) {
        if (effect.value() instanceof OnEffectRemoved onEffectRemoved)
            onEffectRemoved.onRemoved((LivingEntity)(Object)this, statusEffectInstance.getAmplifier());
    }

    @Inject(method = "getEquipmentChanges", at = @At(value = "JUMP", opcode = Opcodes.IFNULL, shift = At.Shift.AFTER))
    private void socwars_checkEquipmentChangesSetBonus(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, @Local Map<EquipmentSlot, ItemStack> map) {
        final MutableBoolean hasSetBonus = new MutableBoolean(false);

        map.forEach((slot, stack) -> { //Kinda gross multi purpous loop to avoid iterating twice

            hasSetBonus.orEquals(stack.get(ModItemComponents.ARMOUR_SET_BONUS) != null);
        });

        if (hasSetBonus.getValue()) {
            this.applyArmourSetBonuses();
        } //Probably make this more robust; maybe another layer of abstraction over the appliesattributemodifiers thing
    }

    @Inject(method = "onEquipmentRemoved", at = @At("HEAD"))
    private void socwars_checkEquipmentRemovedSetBonus(ItemStack removedEquipment, EquipmentSlot slot, AttributeContainer container, CallbackInfo ci) {
        final EquippableComponent equippableComponent = removedEquipment.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || equippableComponent.slot() != slot) return;

        final ArmourSetBonus setBonus = removedEquipment.get(ModItemComponents.ARMOUR_SET_BONUS);
        if (setBonus == null) return; //Same thing here

        this.removeSetBonus(setBonus);
        this.applyArmourSetBonuses();
    }

    @Unique
    private void removeSetBonus(ArmourSetBonus setBonus) {
        setBonus.applyAttributeModifiers((attribute, modifier) -> ifNotNull(this.attributes.getCustomInstance(attribute), instance -> instance.removeModifier(modifier)), 0);
    }

    @Unique
    private void applyArmourSetBonuses() {
        final Map<ArmourSetBonus, Integer> setBonusMap = EquipmentSlot.VALUES.stream().map(slot -> {
            final ItemStack equippedStack = this.getEquippedStack(slot);
            final EquippableComponent equippableComponent = equippedStack.get(DataComponentTypes.EQUIPPABLE);

            return equippableComponent != null && equippableComponent.slot() == slot ? equippedStack.get(ModItemComponents.ARMOUR_SET_BONUS) : null;
        }).filter(Objects::nonNull).collect(Collectors.toMap(Function.identity(), a -> 1, Integer::sum));

        setBonusMap.forEach((setBonus, pieces) -> setBonus.applyAttributeModifiers((attribute, modifier) -> {
            ifNotNull(this.attributes.getCustomInstance(attribute), instance -> {
                instance.removeModifier(modifier.id());
                instance.addTemporaryModifier(modifier);
            });
        }, pieces));
    }
}
