package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Consumer;

public record StasisChamberComponent(Optional<Vec3d> pos) implements ItemUseComponent, SocWarsTooltipAppender {
	public static final StasisChamberComponent DEFAULT = new StasisChamberComponent();

	public static final Codec<StasisChamberComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codecs.optional(Vec3d.CODEC).fieldOf("pos").forGetter(StasisChamberComponent::pos)
	).apply(builder, StasisChamberComponent::new));

	public StasisChamberComponent() {
		this(Optional.empty());
	}

	public StasisChamberComponent(Vec3d pos) {
		this(Optional.of(pos));
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		final ItemStack stack = user.getStackInHand(hand);

		stack.decrementUnlessCreative(1, user);

		if (this.pos.isEmpty()) {
			stack.set(ModItemComponentsReworked.STASIS_CHAMBER, new StasisChamberComponent(user.getPos()));
			stack.set(ModItemComponentsReworked.STASIS_CHAMBER_PRESENT, true);
		} else {
			user.requestTeleport(this.pos.get().x, this.pos.get().y, this.pos.get().z);
			stack.set(ModItemComponentsReworked.STASIS_CHAMBER, DEFAULT);
			stack.set(ModItemComponentsReworked.STASIS_CHAMBER_PRESENT, false);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
		this.pos.ifPresent(pos -> textConsumer.accept(Text.translatable("tooltip.stasis_chamber.stored_pos", String.format("(%s, %s, %s)", Math.round(pos.x), Math.round(pos.y), Math.round(pos.z)))));
	}
}
