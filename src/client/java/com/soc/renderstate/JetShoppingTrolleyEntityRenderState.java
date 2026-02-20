package com.soc.renderstate;

import net.minecraft.client.render.entity.state.EntityRenderState;
import org.joml.Quaternionf;

public class JetShoppingTrolleyEntityRenderState extends EntityRenderState {
    //private static final float RAD2DEG = 0.017453292519943295f;

    private Quaternionf rotation = new Quaternionf();

    public Quaternionf getRotation() {
        return this.rotation;
    }

    public void setRotation(float yaw) {
        this.rotation = new Quaternionf().rotateY(yaw * -0.017453292519943295f);
    }
}
