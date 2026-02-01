package com.soc.renderer;

import com.soc.entities.BWFireballEntity;
import com.soc.lib.RenderHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BWFireballEntityRenderer extends EntityRenderer<BWFireballEntity, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/item/fire_charge.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

    public BWFireballEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrices.push();

        final VertexConsumer consumer = vertexConsumerProvider.getBuffer(LAYER);

        //I would love to have this scale in here but there's no elegant way to scale the fire so it kind of just looks silly if I do
        //matrices.scale(3f, 3f, 3f);

        RenderHelper.renderTexturedQuad(matrices, consumer, super.dispatcher.getRotation(), light);
        super.render(state, matrices, vertexConsumerProvider, light);

        matrices.pop();
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void updateRenderState(BWFireballEntity entity, EntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
    }
}
