package com.soc;

import com.soc.blocks.util.ModBlocks;
import com.soc.entities.util.ModEntities;
import com.soc.gui.screen.HandledScreens;
import com.soc.items.FeatherBlockItem;
import com.soc.networking.S2CReceivers;
import com.soc.renderer.BWFireballEntityRenderer;
import com.soc.renderer.BigTntRenderer;
import com.soc.renderer.MapBlockEntityRenderer;
import com.soc.renderer.CollectibleBlockEntityRenderer;
import com.soc.resourcedata.deserialisation.SkywarsItemData;
import com.soc.resourcedata.listeners.ItemData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Map;

import static com.soc.blocks.blockentities.ModBlockEntities.COLLECTIBLE_BLOCK_ENTITY;
import static com.soc.blocks.blockentities.ModBlockEntities.MAP_BLOCK_ENTITY;

@Environment(EnvType.CLIENT)
public class SocWarsClient implements ClientModInitializer {
	private static KeyBinding KEY_BINDING;

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.NUCLEAR_BOMB, BigTntRenderer::new);
		EntityRendererRegistry.register(ModEntities.HYDROGEN_BOMB, BigTntRenderer::new);
		EntityRendererRegistry.register(ModEntities.BW_FIREBALL_TYPE, BWFireballEntityRenderer::new);

		BlockEntityRendererFactories.register(MAP_BLOCK_ENTITY, MapBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(COLLECTIBLE_BLOCK_ENTITY, CollectibleBlockEntityRenderer::new);

		BlockRenderLayerMap.putBlock(ModBlocks.SPAWN_PLACEHOLDER, BlockRenderLayer.TRANSLUCENT);
		BlockRenderLayerMap.putBlock(ModBlocks.ITSEVOCAT_SKULL, BlockRenderLayer.TRANSLUCENT);

		S2CReceivers.initialise();
		HandledScreens.initialise();

		KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.socwars.print_held_components",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				"category.socwars.debug"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (KEY_BINDING.wasPressed()) {
				final ClientPlayerEntity player = client.player;
				player.getStackInHand(Hand.MAIN_HAND).getComponents().forEach(component -> player.sendMessage(Text.literal(component.toString()), false));

				final Map<Integer, SkywarsItemData> dataMap = ItemData.INSTANCE.getSkywarsItemData().getPoolsForKey(player.getStackInHand(Hand.MAIN_HAND).getItem());
				dataMap.forEach((pool, data) -> player.sendMessage(Text.translatable("debug.skywars_item_weights", pool, data.weightT1(), data.weightT2(), data.weightT3(), data.weightT4()), false));
			}
		});

		WorldRenderEvents.BEFORE_ENTITIES.register(renderContext -> {
			final MinecraftClient client = MinecraftClient.getInstance();

			final boolean mainHandCheck = client.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof FeatherBlockItem;
			final boolean offHandCheck = client.player.getStackInHand(Hand.OFF_HAND).getItem() instanceof FeatherBlockItem;
			if (!mainHandCheck && !offHandCheck) return;

			final float tickProgress = renderContext.tickCounter().getTickProgress(true);
			final Vec3d lookOffset = client.player.getRotationVec(tickProgress).multiply(client.player.getBlockInteractionRange());

			final BlockPos pos = BlockPos.ofFloored(client.player.getEyePos().add(lookOffset));
			if (client.crosshairTarget.getType() == HitResult.Type.BLOCK) return;

			final WorldRenderContext context = WorldRenderContext.getInstance(client.worldRenderer);
			final MatrixStack matrices = context.matrixStack();
			final VertexConsumer consumer = context.consumers().getBuffer(RenderLayer.LINES);

			final int colour = Color.HSBtoRGB(client.world.getTime() / 50f, 1, 1);

			matrices.push();
			matrices.translate(context.camera().getCameraPos().multiply(-1d));
			matrices.translate(Vec3d.of(pos));
			VertexRendering.drawOutline(
					matrices,
					consumer,
					VoxelShapes.fullCube(),
					0d,
					0d,
					0d,
					colour
			);
			matrices.pop();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			try {
				WorldRenderContext context = WorldRenderContext.getInstance(client.worldRenderer);
				MatrixStack matrices = context.matrixStack();
				VertexConsumer consumer = context.consumers().getBuffer(RenderLayer.LINES);

				BlockPos pos = BlockPos.ofFloored(client.player.getPos());

				matrices.push();
				matrices.translate(pos.toCenterPos());
				VertexRendering.drawOutline(
						matrices,
						consumer,
						VoxelShapes.fullCube(),
						0,
						0,
						0,
						0
				);
				matrices.pop();
			} catch (Exception ignored) {}

		});
	}
}
