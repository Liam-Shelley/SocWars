package com.soc.lib.json;

import com.google.gson.JsonObject;
import com.soc.SocWars;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record Time(int ticks) {
    public static final String HOURS_KEY = "hours";
    public static final String MINUTES_KEY = "minutes";
    public static final String SECONDS_KEY = "seconds";
    public static final String TICKS_KEY = "ticks";

    private static final int TICKS_PER_SECOND = 20;
    private static final int TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    private static final int TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;

    public Time(JsonObject object) {
        this(
                getDefaultedInt(object, HOURS_KEY) * TICKS_PER_HOUR +
                getDefaultedInt(object, MINUTES_KEY) * TICKS_PER_MINUTE +
                getDefaultedInt(object, SECONDS_KEY) * TICKS_PER_SECOND +
                getDefaultedInt(object, TICKS_KEY)
        );

        if (this.ticks <= 0) SocWars.LOGGER.info("Read \"Time\" record from NBT with non-positive time. This may be intentional, but I thought it was worth putting in a warning");
    }

    public int seconds() {
        return this.ticks / TICKS_PER_SECOND;
    }

    public int minutes() {
        return this.ticks / TICKS_PER_MINUTE;
    }

    public int hours() {
        return this.ticks / TICKS_PER_HOUR;
    }
}
