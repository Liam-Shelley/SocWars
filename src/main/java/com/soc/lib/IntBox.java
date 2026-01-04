package com.soc.lib;

import net.minecraft.util.math.Vec3i;

public class IntBox {
    @FunctionalInterface
    public interface TriIntConsumer {
        void accept(int x, int y, int z);
    }

    private static final int[] ZERO_ONE = {0, 1};

    private final Vec3i min;
    private final Vec3i max;

    public IntBox(Vec3i corner1, Vec3i corner2) {
        this.min = new Vec3i(
                Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ())
        );
        this.max = new Vec3i(
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ())
        );
    }

    public IntBox(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.min = new Vec3i(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.min(z1, z2)
        );
        this.max = new Vec3i(
                Math.max(x1, x2),
                Math.max(y1, y2),
                Math.max(z1, z2)
        );
    }

    public IntBox(Vec3i size) {
        this.min = Vec3i.ZERO;
        this.max = size;
    }

    public Vec3i getSize() {
        return this.max.subtract(this.min);
    }

    public Vec3i getMin() {
        return this.min;
    }

    public Vec3i getMax() {
        return this.max;
    }

    public void forEach(TriIntConsumer consumer) {
        for (int x = this.min.getX(); x < this.max.getX(); x++) {
            for (int y = this.min.getY(); y < this.max.getY(); y++) {
                for (int z = this.min.getZ(); z < this.max.getZ(); z++) {
                    consumer.accept(x, y, z);
                }
            }
        }
    }

    public void forEachFromZero(TriIntConsumer consumer) {
        for (int x = 0; x < this.getSize().getX(); x++) {
            for (int y = 0; y < this.getSize().getY(); y++) {
                for (int z = 0; z < this.getSize().getZ(); z++) {
                    consumer.accept(x, y, z);
                }
            }
        }
    }

    public IntBox[] splitEight() {
        final IntBox[] split = new IntBox[8];

        for (int x : ZERO_ONE) {
            for (int y : ZERO_ONE) {
                for (int z : ZERO_ONE) {
                    final int minX = this.min.getX() + x * (this.getSize().getX() >> 1);
                    final int minY = this.min.getY() + y * (this.getSize().getY() >> 1);
                    final int minZ = this.min.getZ() + z * (this.getSize().getZ() >> 1);
                    final int maxX = this.min.getX() + (x + 1) * (this.getSize().getX() >> 1);
                    final int maxY = this.min.getY() + (y + 1) * (this.getSize().getY() >> 1);
                    final int maxZ = this.min.getZ() + (z + 1) * (this.getSize().getZ() >> 1);

                    split[x + (y << 1) + (z << 2)] = new IntBox(
                            minX,
                            minY,
                            minZ,
                            maxX,
                            maxY,
                            maxZ
                    );
                }
            }
        }

        return split;
    }
}
