package com.soc.lib;

import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.Queue;

public class SparseVoxelOctree<T> implements OctreeNode {
    private class Offset {
        public Offset() {

        }
    }

    private final int nodeSize; //Must always be some natural 2^n
    private final OctreeNode[] nodes = new OctreeNode[8];

    private SparseVoxelOctree(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    @SuppressWarnings("unchecked")
    public SparseVoxelOctree(CubicList<T> data) {
        final Queue<Pair<IntBox, SparseVoxelOctree<T>>> queue = new LinkedList<>();
        final IntBox paddedSize = new IntBox(pad(data.getSize()));
        this.nodeSize = paddedSize.getSize().getX();
        queue.add(Pair.of(paddedSize, this));

        while (!queue.isEmpty()) {
            final Pair<IntBox, SparseVoxelOctree<T>> current = queue.poll();
            final IntBox currentWindow = current.getLeft();
            final SparseVoxelOctree<T> currentNode = current.getRight();

            final IntBox[] newWindows = currentWindow.splitEight();

            for (int i = 0; i < newWindows.length; i++) {
                final boolean isHomogeneous = isHomogeneous(data, newWindows[i]);
                if (isHomogeneous) {
                    final Vec3i min = newWindows[i].getMin();
                    currentNode.nodes[i] = new Homogeneous<>(data.get(min));
                } else {
                    currentNode.nodes[i] = new SparseVoxelOctree<>(newWindows[i].getSize().getX());
                    queue.add(Pair.of(newWindows[i], (SparseVoxelOctree<T>)currentNode.nodes[i]));
                }
            }
        }
    }

    private static <T> boolean isHomogeneous(CubicList<T> data, IntBox window) {
        final T initial = data.get(window.getMin());

        final Vec3i min = window.getMin();
        final Vec3i max = window.getMax();

        for (int x = min.getX(); x < max.getX(); x++) {
            for (int y = min.getY(); y < max.getY(); y++) {
                for (int z = min.getZ(); z < max.getZ(); z++) {
                    if (data.get(x, y, z) != initial) return false;
                }
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y, int z, Vec3i origin) {
        x -= origin.getX();
        y -= origin.getY();
        z -= origin.getZ();
        OctreeNode candidate = this.getNode(x, y, z);

        while (!candidate.isLeaf()) {
            candidate = ((SparseVoxelOctree<T>)candidate).getNode(x, y, z);
        }

        return ((Homogeneous<T>)candidate).get();
    }

    private OctreeNode getNode(int x, int y, int z) {
        final int halfNodeSize = this.nodeSize >> 1;

        final int internalX = x % this.nodeSize;
        final int internalY = y % this.nodeSize;
        final int internalZ = z % this.nodeSize;

        final boolean topHalfX = internalX >= halfNodeSize;
        final boolean topHalfY = internalY >= halfNodeSize;
        final boolean topHalfZ = internalZ >= halfNodeSize;

        return this.nodes[
                (topHalfX ? 1 : 0) +
                (topHalfY ? 2 : 0) +
                (topHalfZ ? 4 : 0)
        ];
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public int getNodeSize() {
        return this.nodeSize;
    }

    private static Vec3i pad(Vec3i unpadded) {
        int size = Math.max(Math.max(unpadded.getX(), unpadded.getY()), unpadded.getZ());

        size--;
        for (int i = 0; i < 5; i++) {
            size |= size >> (1 << i);
        }
        size++;

        return new Vec3i(size, size, size);
    }


}
