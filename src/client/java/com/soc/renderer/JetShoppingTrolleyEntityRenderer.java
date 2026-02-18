package com.soc.renderer;

import com.soc.entities.JetShoppingTrolleyEntity;
import com.soc.renderstate.JetShoppingTrolleyEntityRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class JetShoppingTrolleyEntityRenderer extends EntityRenderer<JetShoppingTrolleyEntity, JetShoppingTrolleyEntityRenderState> {
    protected JetShoppingTrolleyEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(JetShoppingTrolleyEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public JetShoppingTrolleyEntityRenderState createRenderState() {
        return new JetShoppingTrolleyEntityRenderState();
    }
}
