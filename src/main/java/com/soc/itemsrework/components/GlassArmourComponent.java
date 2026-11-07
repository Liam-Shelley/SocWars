package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import com.soc.util.DamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import static com.soc.lib.SocWarsLib.damageSource;

public record GlassArmourComponent() implements ArmourHitComponent {
	public static final GlassArmourComponent INSTANCE = new GlassArmourComponent();

	public static final Codec<GlassArmourComponent> CODEC = Codec.unit(INSTANCE);

	@Override
	public boolean hit(ItemStack stack, LivingEntity wearer, World world, DamageSource source) {
		if (world instanceof ServerWorld serverWorld && !source.isOf(DamageTypes.GLASS_ARMOUR)) {
			final DamageSource glassSource = damageSource(serverWorld, DamageTypes.GLASS_ARMOUR, wearer);
			final float baseAmount = world.random.nextFloat();

			if (source.getSource() != null) source.getSource().damage(serverWorld, glassSource, baseAmount * 0.25f + 1f);

			wearer.damage(serverWorld, glassSource, baseAmount * 0.2f + 0.5f);
		}

		return true;
	}
}
