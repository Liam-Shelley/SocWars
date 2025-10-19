package com.soc.lib;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import org.joml.Quaternionf;

import java.util.List;

public final class RenderHelper {
    private RenderHelper() {}

    public static final List<VertexInfo> BILLBOARD_ENTITY_VERTICES = List.of(
            new VertexInfo(-0.25f, -0.25f, 0f, 0f, 1f),
            new VertexInfo(0.25f, -0.25f, 0f, 1f, 1f),
            new VertexInfo(0.25f, 0.25f, 0f, 1f, 0f),
            new VertexInfo(-0.25f, 0.25f, 0f, 0f, 0f)
    );

    public record VertexInfo(float x, float y, float z, float u, float v) {
        public void makeVertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, int light) {
            vertexConsumer
                    .vertex(matrix, this.x, this.y, this.z)
                    .color(Colors.WHITE)
                    .texture(this.u, this.v)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(light)
                    .normal(matrix, 0f, 1f, 0f);
        }
    }

    public static void renderTexturedQuad(MatrixStack matrices, VertexConsumer vertexConsumer, Quaternionf rotation, int light) {
        matrices.multiply(rotation);

        MatrixStack.Entry entry = matrices.peek();

        BILLBOARD_ENTITY_VERTICES.forEach(vertex -> vertex.makeVertex(vertexConsumer, entry, light));
    }
}
