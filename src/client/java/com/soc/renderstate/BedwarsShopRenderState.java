package com.soc.renderstate;

import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public class BedwarsShopRenderState extends BipedEntityRenderState {
    private float rotationXZ;

    public float getRotationXZ() {
        return this.rotationXZ;
    }

    public void setRotationXZ(float rotationXZ) {
        this.rotationXZ = rotationXZ;
    }
}
