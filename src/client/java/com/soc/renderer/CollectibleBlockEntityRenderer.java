package com.soc.renderer;

import com.soc.blocks.blockentities.CollectibleBlockEntity;
import com.soc.blocks.util.ModBlocks;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class CollectibleBlockEntityRenderer implements BlockEntityRenderer<CollectibleBlockEntity> {
    private final BlockRenderManager blockRenderManager;

    public CollectibleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.blockRenderManager = context.getRenderManager();
    }

    @Override
    public void render(CollectibleBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        matrices.push();

        matrices.translate(0.5f, 0f, 0.5f);
        matrices.multiply(new Quaternionf().rotateY(entity.getRotation()));
        matrices.translate(-0.5f, 0f, -0.5f);

        this.blockRenderManager.renderBlockAsEntity(ModBlocks.ITSEVOCAT_SKULL.getDefaultState(), matrices, vertexConsumers, light, overlay);

        matrices.pop();
    }
}
