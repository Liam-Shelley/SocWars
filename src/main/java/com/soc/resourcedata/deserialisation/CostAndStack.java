package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Optional;

import static com.soc.lib.json.JsonHelper.*;

public record CostAndStack(Cost cost, ItemStack stack) {
    public static final PacketCodec<RegistryByteBuf, CostAndStack> PACKET_CODEC = PacketCodec.tuple(Cost.PACKET_CODEC, CostAndStack::cost, PacketCodecs.optional(ItemStack.PACKET_CODEC), CostAndStack::optionalStack, CostAndStack::new);

    public static final CostAndStack EMPTY = new CostAndStack(Cost.DEFAULT, ItemStack.EMPTY);

    public CostAndStack(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedItem(object, ItemStack.EMPTY)
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public CostAndStack(Cost cost, Optional<ItemStack> stack) {
        this(cost, stack.orElse(ItemStack.EMPTY));
    }

    private Optional<ItemStack> optionalStack() {
        return this.stack.isEmpty() ? Optional.empty() : Optional.of(this.stack);
    }

    public CostAndStack copy() {
        return new CostAndStack(this.cost, this.stack.copy());
    }
}
