package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.items.DiceOfFate;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DiceOfFatePayload(DiceOfFate.Effect effect) implements CustomPayload {
    public static final Identifier DICE_OF_FATE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "dice_of_fate");
    public static final Id<DiceOfFatePayload> ID = new Id<>(DICE_OF_FATE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, DiceOfFatePayload> CODEC = PacketCodec.tuple(DiceOfFate.Effect.PACKET_CODEC, DiceOfFatePayload::effect, DiceOfFatePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
