package com.soc.lib;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public interface OctreeNode {
    class Homogeneous<T> implements OctreeNode {
        public static final String VALUE_KEY = "value";

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

        @Override
        public NbtElement toNbtBooleanOnly() {
            return NbtByte.of(this.value != null && (boolean)this.value);
        }

        public T get() {
            return this.value;
        }
    }

    default boolean isLeaf() {
        return this.getNodeSize() <= 1;
    };

    int getNodeSize();

    NbtElement toNbtBooleanOnly();
}
