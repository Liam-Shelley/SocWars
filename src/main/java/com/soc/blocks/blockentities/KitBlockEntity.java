package com.soc.blocks.blockentities;
import com.mojang.serialization.Codec;
import com.soc.game.GameKit;
import com.soc.game.manager.GameType;
import com.soc.screenhandler.KitBlockCreationScreenHandler;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static com.soc.blocks.blockentities.ModBlockEntities.KIT_BLOCK_ENTITY;

public class KitBlockEntity extends LockableContainerBlockEntity {
    private GameKit kit;
    private List<GameType> allowedGameTypes;

    public KitBlockEntity(BlockPos pos, BlockState state) {
        super(KIT_BLOCK_ENTITY, pos, state);
        this.kit = new GameKit();
        this.allowedGameTypes = List.of(GameType.values());
    }

    @Override
    protected void writeData(WriteView view) {
        view.put("kit", GameKit.CODEC, this.kit);
        view.put("allowed_game_types", Codec.list(GameType.CODEC), this.allowedGameTypes);
    }

    @Override
    protected void readData(ReadView view) {
        this.kit = view.read("kit", GameKit.CODEC).orElse(new GameKit());
        this.allowedGameTypes = view.read("allowed_game_types", Codec.list(GameType.CODEC)).orElse(List.of(GameType.values()));
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.kit_block_creation");
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
        return new KitBlockCreationScreenHandler(syncId, playerInventory, this.kit, this);
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

    public GameKit getKit() {
        return this.kit;
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {}

    public void setAllowedGameTypes(List<GameType> allowedGameTypes) {
        this.allowedGameTypes = allowedGameTypes;
        this.markDirty();
    }

    public boolean allowsGameType(GameType gameType) {
        return this.allowedGameTypes.contains(gameType);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() instanceof ServerWorld serverWorld) serverWorld.getChunkManager().markForUpdate(this.getPos());
    }

    public List<GameType> getAllowedGameTypes() {
        return this.allowedGameTypes;
    }
}
