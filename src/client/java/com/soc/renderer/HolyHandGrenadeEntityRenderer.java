package com.soc.renderer;

import com.soc.SocWars;
import com.soc.entities.HolyHandGrenadeEntity;
import com.soc.model.EntityModelLayers;
import com.soc.model.HolyHandGrenadeModel;
import com.soc.renderstate.HolyHandGrenadeRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HolyHandGrenadeEntityRenderer extends EntityRenderer<HolyHandGrenadeEntity, HolyHandGrenadeRenderState> {
    public static final Identifier BEAM_TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/entity/holy_hand_grenade_beam.png");

    final HolyHandGrenadeModel model;

    public HolyHandGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new HolyHandGrenadeModel(context.getPart(EntityModelLayers.HOLY_HAND_GRENADE));
    }

    @Override
    public HolyHandGrenadeRenderState createRenderState() {
        return new HolyHandGrenadeRenderState();
    }

    @Override
    public void render(HolyHandGrenadeRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);

        matrices.push();
        matrices.push();

        matrices.translate(0d, 0.75d, 0d);
        matrices.scale(0.5f, -0.5f, -0.5f);
        this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer()), light, 0);

        matrices.pop();

        if (state.getDetonationTimer() > 0f) {
            final float lastTickProgress = MinecraftClient.getInstance().gameRenderer.getCamera().getLastTickProgress();
            final long time = MinecraftClient.getInstance().world.getTime();

            matrices.translate(-0.5d, 0.0d, -0.5d);
            BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, lastTickProgress, 1f, time, 0, 2048, 0xffffffff, 0.15f, 0.2f);
        }

        matrices.pop();
    }

    @Override
    protected void renderLabelIfPresent(HolyHandGrenadeRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {}

    @Override
    public void updateRenderState(HolyHandGrenadeEntity entity, HolyHandGrenadeRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.setDetonationTimer(entity.getDetonationTimer());
    }
}
