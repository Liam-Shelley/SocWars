package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CustomDeathProtectionPayload(int entityId, ItemStack stack) implements CustomPayload {
    public static final Identifier CUSTOM_DEATH_PROTECTION_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "custom_death_protection");
    public static final Id<CustomDeathProtectionPayload> ID = new Id<>(CUSTOM_DEATH_PROTECTION_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, CustomDeathProtectionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            CustomDeathProtectionPayload::entityId,
            ItemStack.OPTIONAL_PACKET_CODEC,
            CustomDeathProtectionPayload::stack,
            CustomDeathProtectionPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
