package com.soc.blocks.blockentities;
import com.soc.game.GameKit;
import com.soc.screenhandler.KitScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import static com.soc.blocks.blockentities.ModBlockEntities.KIT_BLOCK_ENTITY;

public class KitBlockEntity extends LockableContainerBlockEntity {
    private GameKit kit;

    public KitBlockEntity(BlockPos pos, BlockState state) {
        super(KIT_BLOCK_ENTITY, pos, state);
        this.kit = new GameKit();
    }

    @Override
    protected void writeData(WriteView view) {
        view.put("kit", GameKit.CODEC, this.kit);
    }

    @Override
    protected void readData(ReadView view) {
        this.kit = view.read("kit", GameKit.CODEC).orElse(new GameKit());
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.kit");
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.kit.getHeldStacks();
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.kit.setHeldStacks(inventory);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new KitScreenHandler(syncId, playerInventory, this.kit, this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public int size() {
        return this.kit.size();
    }

    public void setKit(GameKit kit) {
        this.kit = kit;
        this.markDirty();
    }

    public GameKit getKit() {
        return this.kit;
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {}
}
