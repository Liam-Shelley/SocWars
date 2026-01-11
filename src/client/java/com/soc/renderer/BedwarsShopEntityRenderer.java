package com.soc.renderer;

import com.soc.SocWars;
import com.soc.entities.BedwarsShopEntity;
import com.soc.entities.BigTntEntity;
import com.soc.model.BedwarsShopEntityModel;
import com.soc.renderstate.BedwarsShopEntityRenderState;
import com.soc.renderstate.BigTntRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import java.util.UUID;

public class BedwarsShopEntityRenderer extends LivingEntityRenderer<BedwarsShopEntity, BedwarsShopEntityRenderState, BedwarsShopEntityModel> {
    private final Identifier skinTexture;

    public BedwarsShopEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BedwarsShopEntityModel(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(EntityModelLayers.PLAYER_SLIM)), 0.5f);
        this.skinTexture = Identifier.of(SocWars.MOD_ID, Math.random() < 0.95d ? "textures/shop_skins/bat_pink.png" : "textures/shop_skins/bat_green.png");
    }

    public void render(BedwarsShopEntityRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(new Quaternionf().rotateY(state.getRotationXZ()));

        super.render(state, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(BedwarsShopEntityRenderState state) {
        return this.skinTexture;
    }

    @Override
    public BedwarsShopEntityRenderState createRenderState() {
        return new BedwarsShopEntityRenderState();
    }

    @Override
    public void updateRenderState(BedwarsShopEntity entity, BedwarsShopEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.setRotationXZ(getPlayerDirection(state, tickProgress));
    }

    @Override
    protected @Nullable Text getDisplayName(BedwarsShopEntity entity) {
        return null;
    }

    private static float getPlayerDirection(BedwarsShopEntityRenderState state, float tickProgress) {
        return 4.71238898038469f-(float)(Math.atan2(state.z - MinecraftClient.getInstance().player.getLerpedPos(tickProgress).z, state.x - MinecraftClient.getInstance().player.getLerpedPos(tickProgress).x));
    }
}
