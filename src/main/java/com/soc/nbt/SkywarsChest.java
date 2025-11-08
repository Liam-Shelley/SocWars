package com.soc.nbt;

import com.soc.game.map.IngameSkywarsChest;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record SkywarsChest(BlockPos pos, int tier, Direction facing) implements ToNbt {
    public static final String LIST_KEY = "skywars_chests";

    public static final String POSITION_KEY = "position";
    public static final String TIER_KEY = "tier";
    public static final String DIRECTION_KEY = "direction";

    public SkywarsChest(NbtCompound nbt) {
        this(
                BlockPos.fromLong(nbt.getLong(POSITION_KEY).orElseThrow()),
                nbt.getInt(TIER_KEY, 1),
                Direction.byIndex(nbt.getInt(DIRECTION_KEY, 0))
        );
    }

    public SkywarsChest(BlockPos pos, IngameSkywarsChest chest) {
        this(
                pos,
                chest.getTier(),
                chest.getFacing()
        );
    }

    @Override
    public NbtCompound toNbt() {
        final NbtCompound nbt = new NbtCompound();
        nbt.putLong(POSITION_KEY, this.pos.asLong());
        nbt.putInt(TIER_KEY, this.tier);
        nbt.putInt(DIRECTION_KEY, this.facing.getIndex());

        return nbt;
    }

    public SkywarsChest withPos(BlockPos pos) {
        return new SkywarsChest(pos, this.tier, this.facing);
    }

    public SkywarsChest subtractPos(BlockPos pos) {
        return this.withPos(this.pos.subtract(pos));
    }
}
