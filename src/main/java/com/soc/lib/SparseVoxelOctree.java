package com.soc.lib;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class SparseVoxelOctree<T> implements OctreeNode {
    public static final String SIZE_KEY = "size";
    public static final String TREE_KEY = "tree";

    private final int nodeSize; //Must always be some natural 2^n
    private final OctreeNode[] nodes = new OctreeNode[8];

    public static <T, B extends ByteBuf> PacketCodec<B, SparseVoxelOctree<T>> packetCodec(PacketCodec<B, T> elementCodec) {
        return new PacketCodec<>() {
            @Override
            public SparseVoxelOctree<T> decode(B buf) {
                if (buf.getChar(buf.readerIndex()) == 'N') {
                    buf.readChar();
                }

                int size = VarInts.read(buf);
                final SparseVoxelOctree<T> tree = new SparseVoxelOctree<>(size);
                for (int i = 0; i < tree.nodes.length; i++) {
                    if (buf.getChar(buf.readerIndex()) == 'N') {
                        buf.readChar();
                        tree.nodes[i] = this.decode(buf);
                    } else {
                        tree.nodes[i] = new Homogeneous<>(elementCodec.decode(buf));
                    }
                }

                return tree;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void encode(B buf, SparseVoxelOctree<T> value) {
                buf.writeChar('N');
                VarInts.write(buf, value.getNodeSize());
                for (OctreeNode node : value.nodes) {
                    if (node.isLeaf()) {
                        elementCodec.encode(buf, ((OctreeNode.Homogeneous<T>)node).get());
                    } else {
                        this.encode(buf, (SparseVoxelOctree<T>)node);
                    }
                }
            }
        };
    }

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

    private static Vec3i pad(Vec3i unpadded) {
        int size = Math.max(Math.max(unpadded.getX(), unpadded.getY()), unpadded.getZ());

        size--;
        for (int i = 0; i < 5; i++) {
            size |= size >> (1 << i);
        }
        size++;

        return new Vec3i(size, size, size);
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

    public T get(Vec3i pos, Vec3i origin) {
        return this.get(pos.getX(), pos.getY(), pos.getZ(), origin);
    }

    private OctreeNode getNode(int x, int y, int z) {
        final int halfNodeSize = this.nodeSize >> 1;

        return this.nodes[
                ((x % this.nodeSize) >= halfNodeSize ? 1 : 0) +
                ((y % this.nodeSize) >= halfNodeSize ? 2 : 0) +
                ((z % this.nodeSize) >= halfNodeSize ? 4 : 0)
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

    /// Should never be called from outside of this class, instead use {@link SparseVoxelOctree#writeToNbtBooleanOnly(String, NbtCompound)}
    @Override
    public NbtElement toNbtBooleanOnly() {
        final NbtList list = new NbtList();
        for (OctreeNode node : this.nodes) {
            list.add(node.toNbtBooleanOnly());
        }
        return list;
    }

    public void writeToNbtBooleanOnly(String key, NbtCompound compound) {
        final NbtCompound svoCompound = new NbtCompound();

        svoCompound.putInt(SIZE_KEY, this.nodeSize);
        svoCompound.put(TREE_KEY, this.toNbtBooleanOnly());

        compound.put(key, svoCompound);
    }

    @Nullable
    public static SparseVoxelOctree<Boolean> fromNbtBooleanOnly(String key, NbtCompound compound) {
        final Optional<NbtCompound> svoCompoundOptional = compound.getCompound(key);
        if (svoCompoundOptional.isEmpty()) return null;
        final NbtCompound svoCompound = svoCompoundOptional.get();

        final Optional<Integer> sizeOptional = svoCompound.getInt(SIZE_KEY);
        if (sizeOptional.isEmpty()) return null;
        final int size = sizeOptional.get();

        return fromNbtBooleanOnly(size, svoCompound.getListOrEmpty(TREE_KEY));
    }

    private static SparseVoxelOctree<Boolean> fromNbtBooleanOnly(int size, NbtList list) {
        final SparseVoxelOctree<Boolean> base = new SparseVoxelOctree<>(size);

        for (int i = 0; i < list.size(); i++) {
            final NbtElement element = list.get(i);
            final Optional<Byte> maybeValue = element.asByte();
            if (maybeValue.isPresent()) {
                base.nodes[i] = new Homogeneous<>(maybeValue.get() > 0);
                continue;
            }
            final Optional<NbtList> maybeList = element.asNbtList();
            if (maybeList.isPresent()) {
                base.nodes[i] = fromNbtBooleanOnly(size >> 1, maybeList.get());
                continue;
            }
        }

        return base;
    }
}
