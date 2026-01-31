package com.soc.renderstate;

import net.minecraft.client.render.entity.state.EntityRenderState;

public class HandGrenadeRenderState extends EntityRenderState {
    private float detonationTimer = 0f;

    public void setDetonationTimer(float detonationTimer) {
        this.detonationTimer = detonationTimer;
    }

    public float getDetonationTimer() {
        return this.detonationTimer;
    }
}
