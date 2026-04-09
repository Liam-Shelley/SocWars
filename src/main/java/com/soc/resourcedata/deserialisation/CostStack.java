package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Optional;

import static com.soc.lib.json.JsonHelper.*;

public record CostStack(Cost cost, ItemStack stack) {
    public static final PacketCodec<RegistryByteBuf, CostStack> PACKET_CODEC = PacketCodec.tuple(Cost.PACKET_CODEC, CostStack::cost, PacketCodecs.optional(ItemStack.PACKET_CODEC), CostStack::optionalStack, CostStack::new);

    public static final CostStack EMPTY = new CostStack(Cost.DEFAULT, ItemStack.EMPTY);

    public CostStack(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedItem(object, ItemStack.EMPTY)
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public CostStack(Cost cost, Optional<ItemStack> stack) {
        this(cost, stack.orElse(ItemStack.EMPTY));
    }

    private Optional<ItemStack> optionalStack() {
        return this.stack.isEmpty() ? Optional.empty() : Optional.of(this.stack);
    }

    public CostStack copy() {
        return new CostStack(this.cost, this.stack.copy());
    }
}
