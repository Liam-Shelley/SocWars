package com.soc.game.map;

import net.minecraft.util.DyeColor;

public enum DyeColourWithEmpty {
    WHITE(DyeColor.WHITE),
    ORANGE(DyeColor.ORANGE),
    MAGENTA(DyeColor.MAGENTA),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE),
    YELLOW(DyeColor.YELLOW),
    LIME(DyeColor.LIME),
    PINK(DyeColor.PINK),
    GRAY(DyeColor.GRAY),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY),
    CYAN(DyeColor.CYAN),
    PURPLE(DyeColor.PURPLE),
    BLUE(DyeColor.BLUE),
    BROWN(DyeColor.BROWN),
    GREEN(DyeColor.GREEN),
    RED(DyeColor.RED),
    BLACK(DyeColor.BLACK),
    EMPTY(null);

    private DyeColor colour;

    DyeColourWithEmpty(DyeColor colour) {
        this.colour = colour;
    }

    public static DyeColourWithEmpty fromOrdinal(int ordinal) {
        final DyeColourWithEmpty[] values = DyeColourWithEmpty.values();
        return values[ordinal < values.length ? ordinal : 0];
    }

    public static DyeColourWithEmpty fromDyeColour(DyeColor colour) {
        return colour == null ? EMPTY : fromOrdinal(colour.ordinal());
    }
}
