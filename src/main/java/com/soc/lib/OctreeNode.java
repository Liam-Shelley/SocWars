package com.soc.lib;

public interface OctreeNode {
    class Homogeneous<T> implements OctreeNode {
        private final T value;

        public Homogeneous(T value) {
            this.value = value;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        @Override
        public int getNodeSize() {
            return 1;
        }

        T get() {
            return this.value;
        }
    }

    default boolean isLeaf() {
        return this.getNodeSize() <= 1;
    };

    int getNodeSize();
}
