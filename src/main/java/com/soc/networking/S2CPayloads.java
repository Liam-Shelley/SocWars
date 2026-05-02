package com.soc.networking;

import com.soc.networking.s2c.*;
import com.soc.networking.s2c.bedwars.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class S2CPayloads {
    public static void initialise() {
        PayloadTypeRegistry.playS2C().register(JoinQueuePayload.ID, JoinQueuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveQueuePayload.ID, LeaveQueuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerDataPayload.ID, PlayerDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AddVelocityPayload.ID, AddVelocityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(JoinBedwarsPayload.ID, JoinBedwarsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveBedwarsPayload.ID, LeaveBedwarsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BedBreakPayload.ID, BedBreakPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BedwarsIndividualShopDataPayload.ID, BedwarsIndividualShopDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BedwarsTeamShopDataPayload.ID, BedwarsTeamShopDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateHotbarPayload.ID, UpdateHotbarPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UseTrapOrAbilityPayload.ID, UseTrapOrAbilityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SmokescreenPayload.ID, SmokescreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BatchParticlePayload.ID, BatchParticlePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BlockProtectionPayload.ID, BlockProtectionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(JumpscarePayload.ID, JumpscarePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SilencePayload.ID, SilencePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EventQueuePayload.ID, EventQueuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveGamePayload.ID, LeaveGamePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SetAnglesPayload.ID, SetAnglesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TeamEliminatedPayload.ID, TeamEliminatedPayload.CODEC);
    }
}
