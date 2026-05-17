package com.soc.networking;

import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.networking.c2s.*;
import com.soc.player.PlayerDataManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class C2SReceivers {
    public static void initialise() {
        ServerPlayNetworking.registerGlobalReceiver(MapBlockUpdatePayload.ID, (payload, context) -> {
            final BlockEntity blockEntity = payload.getBlockEntity(context);

            if (blockEntity instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.setRegionSize(BlockPos.fromLong(payload.regionSize()).mutableCopy());
                mapBlockEntity.setMapName(payload.mapName());
                mapBlockEntity.setMapType(GameType.fromOrdinal(payload.mapType()));
                mapBlockEntity.setBlockProtection(payload.blockProtection());
                mapBlockEntity.setFields(payload.fields());

                context.player().getWorld().getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MapBlockStructureCheckPayload.ID, (payload, context) -> {
            final BlockEntity blockEntity = payload.getBlockEntity(context);

            if (blockEntity instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.checkStructure();

                context.player().getWorld().getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MapBlockSaveMapPayload.ID, (payload, context) -> {
            final BlockEntity blockEntity = payload.getBlockEntity(context);

            if (blockEntity instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.saveMap(context.player());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(KitBlockUpdatePayload.ID, ((payload, context) -> {
            final BlockEntity blockEntity = payload.getBlockEntity(context);

            if (blockEntity instanceof KitBlockEntity kitBlockEntity) {
                kitBlockEntity.setAllowedGameTypes(payload.allowedGameTypes());
            }
        }));
        ServerPlayNetworking.registerGlobalReceiver(KitSelectionPayload.ID, ((payload, context) -> {
            final BlockEntity blockEntity = payload.getBlockEntity(context);

            if (blockEntity instanceof KitBlockEntity kitBlockEntity) {
                PlayerDataManager.getPlayerData(context.player()).setKits(kitBlockEntity.getKit(), payload.selectedGameTypes());

                final MutableText message = Text.translatable("message.kit_selection", kitBlockEntity.getKit().getName());
                for (GameType gameType : payload.selectedGameTypes()) {
                    message.append("\n  ").append(gameType.getVariantName());
                }
                context.player().sendMessage(message);
            }
        }));
    }
}
