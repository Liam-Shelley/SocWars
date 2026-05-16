package com.soc.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class GameKit implements Inventory {
    public static int ITEM_SLOTS = 9;

    public static final PacketCodec<RegistryByteBuf, GameKit> PACKET_CODEC = PacketCodec.tuple(
            ItemStack.OPTIONAL_LIST_PACKET_CODEC, kit -> kit.items,
            PacketCodecs.collection(ArrayList::new, StatusEffectInstance.PACKET_CODEC), kit -> kit.effects,
            GameKit::new
    );

    public static final Codec<GameKit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("items").orElse(DefaultedList.ofSize(0, ItemStack.EMPTY)).forGetter(kit -> kit.items),
            Codec.list(StatusEffectInstance.CODEC).fieldOf("effects").orElse(new ArrayList<>()).forGetter(kit -> kit.effects)
    ).apply(instance, GameKit::new));

    private DefaultedList<ItemStack> items;
    private List<StatusEffectInstance> effects;

    private boolean isDirty;

    public GameKit(DefaultedList<ItemStack> items, List<StatusEffectInstance> effects) {
        this.items = items;
        this.effects = effects;
    }

    private GameKit(List<ItemStack> items, List<StatusEffectInstance> effects) {
        this.items = DefaultedList.ofSize(ITEM_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            this.items.set(i, items.get(i));
        }
        this.effects = new ArrayList<>();
    }

    public GameKit() {
        this(DefaultedList.ofSize(ITEM_SLOTS, ItemStack.EMPTY), new ArrayList<>());
    }

    public void apply(ServerPlayerEntity player) {
        this.items.forEach(item -> player.giveItemStack(item.copy()));
        this.effects.forEach(effect -> player.addStatusEffect(new StatusEffectInstance(effect)));
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.items, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        final ItemStack stack = this.items.get(slot);
        this.items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.items.set(slot, stack);
    }

    @Override
    public void markDirty() {
        this.isDirty = true;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.items.clear();
        this.effects.clear();
    }

    public DefaultedList<ItemStack> getHeldStacks() {
        return this.items;
    }

    public void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.items = inventory;
    }
}
