package com.soc.networking;

import com.soc.game.BedwarsTeamsHUD;
import com.soc.networking.s2c.*;
import com.soc.networking.s2c.bedwars.BedBreakPayload;
import com.soc.networking.s2c.bedwars.JoinBedwarsPayload;
import com.soc.networking.s2c.bedwars.LeaveBedwarsPayload;
import com.soc.networking.s2c.bedwars.ShopDataPayload;
import com.soc.player.PlayerDataManager;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class S2CReceivers {
    public static void initialise() {
        ClientPlayNetworking.registerGlobalReceiver(JoinQueuePayload.ID, (payload, context) -> {
                context.player().sendMessage(Text.translatable("queue.join", payload.queue()), false);
        });
        ClientPlayNetworking.registerGlobalReceiver(LeaveQueuePayload.ID, (payload, context) -> {
                context.player().sendMessage(Text.translatable("queue.leave", payload.queue()), false);
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDataPayload.ID, (payload, context) -> {
                PlayerDataManager.setPlayerData(context.player(), payload.playerData());
        });
        ClientPlayNetworking.registerGlobalReceiver(AddVelocityPayload.ID, (payload, context) -> {
                context.player().addVelocity(payload.velocity());
        });
        ClientPlayNetworking.registerGlobalReceiver(JoinBedwarsPayload.ID, (payload, context) -> {
            BedwarsTeamsHUD.joinGame(payload.teams());
        });
        ClientPlayNetworking.registerGlobalReceiver(LeaveBedwarsPayload.ID, (payload, context) -> {
            BedwarsTeamsHUD.leaveGame();
        });
        ClientPlayNetworking.registerGlobalReceiver(BedBreakPayload.ID, (payload, context) -> {
            BedwarsTeamsHUD.breakBed(payload.team());
        });
        ClientPlayNetworking.registerGlobalReceiver(ShopDataPayload.ID, (payload, context) -> {
            final ScreenHandler screenHandler = context.player().currentScreenHandler;
            if (screenHandler.syncId != payload.syncId() || !(screenHandler instanceof BedwarsShopScreenHandler)) return;

            ((BedwarsShopScreenHandler) screenHandler).setShopContents(payload);
        });
    }
}
