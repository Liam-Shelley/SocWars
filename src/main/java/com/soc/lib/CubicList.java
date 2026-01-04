package com.soc.lib;

import net.minecraft.util.math.Vec3i;

public class CubicList<T> {
    @FunctionalInterface
    public interface ValueMapper<T> {
        T map(int x, int y, int z);
    }

    private final Vec3i size;
    private final T[][][] contents;

    @SuppressWarnings("unchecked")
    public CubicList(Vec3i size, ValueMapper<T> retriever) {
        this.size = size;
        this.contents = (T[][][])new Object[size.getX()][size.getY()][size.getZ()];
        for (int x = 0; x < this.size.getX(); x++) {
            for (int y = 0; y < this.size.getY(); y++) {
                for (int z = 0; z < this.size.getZ(); z++) {
                    this.contents[x][y][z] = retriever.map(x, y, z);
                }
            }
        }
    }

    public T get(int x, int y, int z) {
        if (
                x < 0 || x >= this.size.getX() ||
                y < 0 || y >= this.size.getY() ||
                z < 0 || z >= this.size.getZ()
        ) return null;

        return this.contents[x][y][z];
    }

    public T get(Vec3i index) {
        return this.get(index.getX(), index.getY(), index.getZ());
    }

    public Vec3i getSize() {
        return this.size;
    }

    public SparseVoxelOctree<T> asOctree() {
        return new SparseVoxelOctree<>(this);
    }
}
