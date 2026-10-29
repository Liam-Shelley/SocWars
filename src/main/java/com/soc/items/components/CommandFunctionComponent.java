package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.itemsrework.components.ItemHitAllComponent;
import com.soc.itemsrework.components.ItemUseComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Objects;

public record CommandFunctionComponent(String leftClick, String rightClick) implements ItemUseComponent, ItemHitAllComponent {
	public static final Codec<CommandFunctionComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.STRING.fieldOf("left_click").forGetter(CommandFunctionComponent::leftClick),
			Codec.STRING.fieldOf("right_click").forGetter(CommandFunctionComponent::rightClick)
	).apply(builder, CommandFunctionComponent::new));

	private static void runFunction(PlayerEntity player, String function) {
		if (player.getWorld().isClient || function.isBlank()) return;

		final ServerCommandSource source = new ServerCommandSource(
				CommandOutput.DUMMY,
				player.getPos(),
				player.getRotationClient(),
				(ServerWorld)player.getWorld(),
				2,
				player.getName().getString(),
				player.getDisplayName(),
				player.getServer(),
				player
		);

		Objects.requireNonNull(player.getServer()).getCommandManager().executeWithPrefix(source, "function " + function);
	}

	@Override
	public void hit(PlayerEntity player, ItemStack stack) {
		runFunction(player, this.leftClick);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		runFunction(user, this.rightClick);
		return ActionResult.CONSUME;
	}
}
