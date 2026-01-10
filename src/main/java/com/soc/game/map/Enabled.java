package com.soc.game.map;

import net.minecraft.text.Text;

public enum Enabled {
    ENABLED(true),
    DISABLED(false);

    private final boolean value;

    Enabled(boolean value) {
        this.value = value;
    }

    public boolean booleanValue() {
        return this.value;
    }

    public static Enabled fromBoolean(boolean value) {
        return value ? ENABLED : DISABLED;
    }

    public Text getVariantName() {
        return value ? Text.translatable("gui.enabled") : Text.translatable("gui.disabled");
    }
}
