package com.soc.gui.hud;

import org.jetbrains.annotations.Nullable;

public class Reference<T> {
    public Reference(@Nullable T value) {
        this.value = value;
    }

    private @Nullable T value;

    public void set(@Nullable T value) {
        this.value = value;
    }

    public @Nullable T get() {
        return this.value;
    }

    public boolean isNotNull() {
        return this.value != null;
    }

    public void annul() {
        this.value = null;
    }
}
