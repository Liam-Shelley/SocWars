package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.SocWars;
import com.soc.events.ModEvents;
import com.soc.networking.ModPacketCodecs;
import com.soc.util.ModBlockTags;
import com.soc.util.ModCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Morph(BlockState blockState, Box boundingBox, float health) {
	private static final List<Property<?>> CYCLING_PROPERTIES = List.of(
			Properties.AXIS,
			Properties.FACING,
			Properties.HORIZONTAL_FACING,
			Properties.BLOCK_FACE,
			Properties.ROTATION,
			Properties.SLAB_TYPE
	);

	public static final Codec<Morph> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockState.CODEC.fieldOf("block_state").orElse(null).forGetter(Morph::blockState),
			ModCodecs.BOX.fieldOf("bounding_box").orElse(VoxelShapes.fullCube().getBoundingBox()).forGetter(Morph::boundingBox),
			Codecs.POSITIVE_FLOAT.fieldOf("health").orElse(20f).forGetter(Morph::health)
	).apply(instance, Morph::new));

	public static final PacketCodec<ByteBuf, Morph> PACKET_CODEC = PacketCodec.tuple(
			ModPacketCodecs.BLOCK_STATE, Morph::blockState,
			ModPacketCodecs.BOX, Morph::boundingBox,
			PacketCodecs.FLOAT, Morph::health,
			Morph::new
	);

	public static final Identifier HEALTH_MODIFIER_ID = Identifier.of(SocWars.MOD_ID, "morph_health");

	@Nullable
	public static Morph of(BlockState blockState, BlockView world) {
		return blockState == null ? null : new Morph(blockState, makeBoundingBox(blockState, world), calculateMaxHealth(blockState, world));
	}

	public boolean hasProperty(Property<?> property) {
		return this.blockState.contains(property);
	}

	private static Box makeBoundingBox(BlockState blockState, BlockView world) {
		final VoxelShape shape = getShape(blockState, world);
		final Box preShrinkBox = shape.getBoundingBox().stretch(0d, -shape.getMin(Direction.Axis.Y), 0d);
		return new Box(Math.max(preShrinkBox.minX, 0.08d), preShrinkBox.minY, Math.max(preShrinkBox.minZ, 0.08d), Math.min(preShrinkBox.maxX, 0.92d), preShrinkBox.maxY, Math.min(preShrinkBox.maxZ, 0.92d));
	}

	private static float calculateMaxHealth(BlockState blockState, BlockView world) {
		return (float)Math.clamp(Math.sqrt(calculateBlockVolume(blockState, world)) * 20f, 2f, 40f);
	}

	public static double calculateBlockVolume(BlockState blockState, BlockView world) {
		final VoxelShape shape = getShape(blockState, world);
		return shape.getBoundingBoxes().stream().map(Morph::getBoxVolume).reduce(0d, Double::sum);
	}

	private static VoxelShape getShape(BlockState blockState, BlockView world) {
		final VoxelShape baseShape = blockState.getCollisionShape(world, null);
		return baseShape.isEmpty() ? blockState.getOutlineShape(world, null) : baseShape;
	}

	private static double getBoxVolume(Box box) {
		return box.getLengthX() * box.getLengthY() * box.getLengthZ();
	}

	public boolean shouldSnap(boolean isSneaking, BlockView world, BlockPos pos) {
		final BlockState stateAtPos = world.getBlockState(pos);
		return isSneaking && !this.blockState.isReplaceable() && stateAtPos.isReplaceable();
	}

	public void rotate(PlayerEntity player, PlayerData playerData, boolean reverse) { //TODO: Cycle between multiple properties of a block when applicable
		CYCLING_PROPERTIES.stream().filter(this::hasProperty).findFirst().ifPresent(property -> {
			playerData.setMorph(DebugStickItem.cycle(this.blockState(), property, reverse), player);
			player.getItemCooldownManager().set(player.getStackInHand(Hand.OFF_HAND), 3 * 20);
		});
	}

	public static void clearMorph(ServerPlayerEntity serverPlayer) { //Some serious spaghetti code going on here
		PlayerDataManager.getPlayerData(serverPlayer).clearMorph(serverPlayer);
	}

	public static void rotateMorph(ServerPlayerEntity serverPlayer, boolean reverse) {
		PlayerDataManager.getPlayerData(serverPlayer).rotateMorph(serverPlayer, reverse);
	}

	public static boolean attemptMorph(ServerPlayerEntity player, BlockState blockState) {
		if (blockState.isIn(ModBlockTags.DISALLOW_MORPH)) {
			player.sendMessage(Text.literal("Nice try ;)"));
			return false;
		}

		final boolean allowMorph = ModEvents.ON_PLAYER_MORPHED.invoker().onPlayerMorphed(player, blockState);
		if (allowMorph) {
			PlayerDataManager.getPlayerData(player).setMorph(blockState, player);
			return true;
		} else {
			return false;
		}
	}
}