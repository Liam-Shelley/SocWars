package com.soc.game.map;

import com.google.gson.JsonObject;
import com.soc.SocWars;
import net.minecraft.util.DyeColor;

import java.util.function.Consumer;

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
    EMPTY((DyeColor)null);

    private final DyeColor colour;

    DyeColourWithEmpty(DyeColor colour) {
        this.colour = colour;
    }

    DyeColourWithEmpty(JsonObject object) {
        DyeColor colour = null;
        try {
            colour = DyeColor.byId(object.getAsString(), null);
        } catch (Exception ignored) {
            SocWars.LOGGER.warn("Failed to parse \"{}\" as a DyeColourWithEmpty, returning Empty", object.toString());
        }

        this.colour = colour;
    }

    public static DyeColourWithEmpty fromOrdinal(int ordinal) {
        final DyeColourWithEmpty[] values = DyeColourWithEmpty.values();
        return values[ordinal < values.length ? ordinal : 0];
    }

    public static DyeColourWithEmpty fromDyeColour(DyeColor colour) {
        return colour == null ? EMPTY : fromOrdinal(colour.ordinal());
    }

    public DyeColor getColour() {
        return this.colour;
    }

    public boolean isPresent() {
        return this.colour != null;
    }

    public void ifPresent(Consumer<DyeColor> function) {
        if (this.colour != null) function.accept(this.colour);
    }

    public void ifPresentOrElse(Consumer<DyeColor> function, Runnable elseFunction) {
        if (this.colour != null) {
            function.accept(this.colour);
        } else {
            elseFunction.run();
        }
    }

    public String toStringWithEmptyAlias(String alias) {
        return this.colour == null ? alias : this.colour.toString();
    }
}
