package com.soc.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public record SpawnPosition(BlockPos pos, int colour) implements ToNbt {
    public static final String LIST_KEY = "spawn_positions";

    public static final String POSITION_KEY = "position";
    public static final String COLOUR_KEY = "colour";

    public SpawnPosition(NbtCompound nbt) {
        this(
                BlockPos.fromLong(nbt.getLong(POSITION_KEY).orElseThrow()),
                nbt.getInt(COLOUR_KEY, 16)
        );
    }

    @Override
    public NbtCompound toNbt() {
        final NbtCompound nbt = new NbtCompound();
        nbt.putLong(POSITION_KEY, this.pos.asLong());
        nbt.putInt(COLOUR_KEY, this.colour);

        return nbt;
    }

    public DyeColor dyeColour() {
        return DyeColor.byIndex(this.colour);
    }

    public SpawnPosition withPos(BlockPos pos) {
        return new SpawnPosition(pos, this.colour);
    }

    public SpawnPosition subtractPos(BlockPos pos) {
        return this.withPos(this.pos.subtract(pos));
    }
}
