package com.soc.networking;

import com.soc.game.BedwarsTeamsHUD;
import com.soc.networking.s2c.*;
import com.soc.networking.s2c.bedwars.*;
import com.soc.player.PlayerDataManager;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import java.util.List;

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
            if (screenHandler.syncId == payload.syncId() && screenHandler instanceof BedwarsIndividualShopScreenHandler bedwarsIndividualShopScreenHandler) {
                bedwarsIndividualShopScreenHandler.setShopContents(payload);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(UpdateHotbarPayload.ID, (payload, context) -> {
            final PlayerEntity player = context.player();
            final PlayerScreenHandler screenHandler = player.playerScreenHandler;
            final List<ItemStack> contents = payload.contents();

            for (int i = 0; i < payload.contents().size(); i++) {
                screenHandler.setStackInSlot(i + 36, payload.revision(), contents.get(i));
            }
        });
    }
}
