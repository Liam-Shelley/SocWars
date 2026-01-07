package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public record UpdateHotbarPayload(int syncId, int revision, List<ItemStack> contents) implements CustomPayload {
    public static final Identifier UPDATE_HOTBAR_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "update_hotbar");
    public static final Id<UpdateHotbarPayload> ID = new Id<>(UPDATE_HOTBAR_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, UpdateHotbarPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, UpdateHotbarPayload::syncId, PacketCodecs.INTEGER, UpdateHotbarPayload::revision, ItemStack.OPTIONAL_LIST_PACKET_CODEC, UpdateHotbarPayload::contents, UpdateHotbarPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public UpdateHotbarPayload(int syncId, int revision, PlayerInventory inventory) {
        this(syncId, revision, getHotbarList(inventory));
    }

    private static List<ItemStack> getHotbarList(PlayerInventory inventory) {
        final List<ItemStack> items = inventory.getMainStacks().stream().limit(9).collect(Collectors.toList());
        items.add(inventory.getStack(40));
        return items;
    }
}
