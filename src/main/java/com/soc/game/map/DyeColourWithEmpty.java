package com.soc.game.map;

import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Consumer;

public enum DyeColourWithEmpty implements StringIdentifiable {
    WHITE(DyeColor.WHITE, "white"),
    ORANGE(DyeColor.ORANGE, "orange"),
    MAGENTA(DyeColor.MAGENTA, "magenta"),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE, "light_blue"),
    YELLOW(DyeColor.YELLOW, "yellow"),
    LIME(DyeColor.LIME, "lime"),
    PINK(DyeColor.PINK, "pink"),
    GRAY(DyeColor.GRAY, "grey"),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY, "light_grey"),
    CYAN(DyeColor.CYAN, "cyan"),
    PURPLE(DyeColor.PURPLE, "purple"),
    BLUE(DyeColor.BLUE, "blue"),
    BROWN(DyeColor.BROWN, "brown"),
    GREEN(DyeColor.GREEN, "green"),
    RED(DyeColor.RED, "red"),
    BLACK(DyeColor.BLACK, "black"),
    EMPTY(null, "empty");

    private final DyeColor colour;
    private final String id;

    DyeColourWithEmpty(DyeColor colour, String id) {
        this.colour = colour;
        this.id = id;
    }

    /*
    public static DyeColourWithEmpty fromJson(JsonObject object) {
        DyeColor colour = null;
        try {
            colour = DyeColor.byId(object.getAsString(), null);
        } catch (Exception ignored) {
            SocWars.LOGGER.warn("Failed to parse \"{}\" as a DyeColourWithEmpty, returning Empty", object.toString());
        }
    }
     */

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

    @Override
    public String asString() {
        return this.id;
    }
}
