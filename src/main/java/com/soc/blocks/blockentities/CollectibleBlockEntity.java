package com.soc.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

import static com.soc.blocks.blockentities.ModBlockEntities.COLLECTIBLE_BLOCK_ENTITY;
import static net.minecraft.block.SkullBlock.ROTATION;

public class CollectibleBlockEntity extends BlockEntity {
    private RegistryEntry<Item> collectible;
    final int rotation;

    public CollectibleBlockEntity(BlockPos pos, BlockState state) {
        super(COLLECTIBLE_BLOCK_ENTITY, pos, state);
        this.rotation = state.get(ROTATION);
    }

    public static void initialise() {}

    public void setCollectible(RegistryEntry<Item> collectible) {
        this.collectible = collectible;
        this.markDirty();
    }

    public float getRotation() {
        return this.rotation * -0.39269908169872414f + 3.141592653589793f; // -PI/8 + PI
    }

    public RegistryEntry<Item> getCollectible() {
        return this.collectible;
    }

    @Override
    protected void writeData(WriteView view) {
        if (this.collectible != null) view.put("collectible", Item.ENTRY_CODEC, this.collectible);

        super.writeData(view);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        this.collectible = view.read("collectible", Item.ENTRY_CODEC).orElse(null);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
