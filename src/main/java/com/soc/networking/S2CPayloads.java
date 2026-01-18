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
        PayloadTypeRegistry.playS2C().register(BedwarsShopDataPayload.ID, BedwarsShopDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BedwarsTeamShopDataPayload.ID, BedwarsTeamShopDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateHotbarPayload.ID, UpdateHotbarPayload.CODEC);
    }
}
