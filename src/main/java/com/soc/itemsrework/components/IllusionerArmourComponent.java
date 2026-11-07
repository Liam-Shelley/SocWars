package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import com.soc.player.PlayerDataManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Objects;

public record IllusionerArmourComponent() implements ArmourHitComponent {
	public static final IllusionerArmourComponent INSTANCE = new IllusionerArmourComponent();

	public static final Codec<IllusionerArmourComponent> CODEC = Codec.unit(INSTANCE);

	@Override
	public boolean hit(ItemStack stack, LivingEntity wearer, World world, DamageSource source) {
		if (wearer instanceof ServerPlayerEntity serverPlayer) {
			final EquipmentSlot slot = Objects.requireNonNull(stack.get(DataComponentTypes.EQUIPPABLE)).slot();

			PlayerDataManager.getPlayerData(serverPlayer).addIllusion(world, slot);
		}

		return true;
	}
}
