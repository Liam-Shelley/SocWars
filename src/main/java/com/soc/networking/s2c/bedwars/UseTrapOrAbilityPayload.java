package com.soc.networking.s2c.bedwars;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UseTrapOrAbilityPayload(long nextTime, int duration, boolean isAbility) implements CustomPayload {
    public static final Identifier USE_TRAP_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "use_trap_ability");
    public static final Id<UseTrapOrAbilityPayload> ID = new Id<>(USE_TRAP_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, UseTrapOrAbilityPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.LONG, UseTrapOrAbilityPayload::nextTime,
            PacketCodecs.INTEGER, UseTrapOrAbilityPayload::duration,
            PacketCodecs.BOOLEAN, UseTrapOrAbilityPayload::isAbility,
            UseTrapOrAbilityPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
