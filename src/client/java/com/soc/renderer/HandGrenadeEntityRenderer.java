package com.soc.renderer;

import com.soc.entities.HandGrenadeEntity;
import com.soc.model.EntityModelLayers;
import com.soc.model.HolyHandGrenadeModel;
import com.soc.renderstate.HandGrenadeRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class HandGrenadeEntityRenderer<T extends HandGrenadeEntity> extends EntityRenderer<T, HandGrenadeRenderState> {
    private final HolyHandGrenadeModel model;

    public HandGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new HolyHandGrenadeModel(context.getPart(EntityModelLayers.HOLY_HAND_GRENADE));
    }

    @Override
    public HandGrenadeRenderState createRenderState() {
        return new HandGrenadeRenderState();
    }

    protected Model getModel() {
        return this.model;
    }

    @Override
    public void render(HandGrenadeRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);

        matrices.push();

        matrices.translate(0d, 0.75d, 0d);
        matrices.scale(0.5f, -0.5f, -0.5f);
        this.getModel().render(matrices, vertexConsumers.getBuffer(this.model.getLayer()), light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

    @Override
    protected void renderLabelIfPresent(HandGrenadeRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {}

    @Override
    public void updateRenderState(T entity, HandGrenadeRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.setDetonationTimer(entity.getDetonationTimer());
    }
}
