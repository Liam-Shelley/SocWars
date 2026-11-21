package com.soc.networking;

import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.blocks.blockentities.MapBlockEntity;
import com.soc.events.ModEvents;
import com.soc.events.OnDeathNoteKill;
import com.soc.game.manager.GameType;
import com.soc.game.manager.GamesManager;
import com.soc.items.SocWarsItem;
import com.soc.items.components.ModItemComponents;
import com.soc.lib.Events;
import com.soc.networking.c2s.*;
import com.soc.networking.s2c.OpenQueueScreenPayload;
import com.soc.networking.s2c.QueuePayload;
import com.soc.networking.s2c.ReceiveDeathNotePayload;
import com.soc.player.DeathNoteState;
import com.soc.player.PlayerDataManager;
import com.soc.util.ModDamageTypes;
import com.soc.util.Sounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import static com.soc.lib.SocWarsLib.forceKillTarget;
import static com.soc.lib.SocWarsLib.ifNotNull;

public class C2SReceivers {
    public static void initialise() {
        queues();
        mapBlock();
        kitBlock();
        ServerPlayNetworking.registerGlobalReceiver(OnAttackButtonPressedPayload.ID, (payload, context) -> {
            ((SocWarsItem)payload.stack().getItem()).onAttackButtonPressed(context.player(), payload.stack());
        });
        ServerPlayNetworking.registerGlobalReceiver(UpdateDoubleJumpPayload.ID, (payload, context) -> {
            final ServerPlayerEntity player = context.player();
            for (Hand hand : Hand.values()) {
                final ItemStack stack = player.getStackInHand(hand);
                ifNotNull(stack.get(ModItemComponents.DOUBLE_JUMP), doubleJump -> stack.set(ModItemComponents.DOUBLE_JUMP, payload.allow()));
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(DeathNoteKillPayload.ID, (payload, context) -> {
            final ServerPlayerEntity target = context.server().getPlayerManager().getPlayer(payload.uuid());
            final OnDeathNoteKill.Result result = ModEvents.ON_DEATH_NOTE_KILL.invoker().tryKill(target, context.player());

            if (target != null && result == OnDeathNoteKill.Result.SUCCESS) {
                context.player().getActiveItem().decrementUnlessCreative(1, context.player());

                target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 20, 255, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 20, 255, false, false));
                target.playSoundToPlayer(Sounds.DEATH_NOTE_BUILDUP, SoundCategory.PLAYERS, 1f, 1f);
                ServerPlayNetworking.send(target, new ReceiveDeathNotePayload());

                DeathNoteState.addName(target);
                Events.getInstance().scheduleEvent(() -> forceKillTarget(target, context.player(), ModDamageTypes.DEATH_NOTE), 20 * 20);
            }
        });
    }

    private static void kitBlock() {
        ServerPlayNetworking.registerGlobalReceiver(KitBlockUpdatePayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof KitBlockEntity kitBlockEntity) {
                kitBlockEntity.update(payload);
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(KitSelectionPayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof KitBlockEntity kitBlockEntity) {
                final boolean playerOwnsKit = PlayerDataManager.getPlayerData(context.player()).setKits(kitBlockEntity.getKit(), payload);

				kitBlockEntity.getKit().generateSelectionMessage(payload, playerOwnsKit, context.player());
			}
        });
        ServerPlayNetworking.registerGlobalReceiver(BuyKitPayload.ID, (payload, context) -> {
            PlayerDataManager.buyKit(context.player(), payload);
        });
    }

    private static void mapBlock() {
        ServerPlayNetworking.registerGlobalReceiver(MapBlockUpdatePayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.setRegionSize(BlockPos.fromLong(payload.regionSize()).mutableCopy());
                mapBlockEntity.setMapName(payload.mapName());
                mapBlockEntity.setMapType(GameType.fromOrdinal(payload.mapType()));
                mapBlockEntity.setBlockProtection(payload.blockProtection());
                mapBlockEntity.setFields(payload.fields());

                context.player().getWorld().getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MapBlockStructureCheckPayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.checkStructure();

                context.player().getWorld().getChunkManager().markForUpdate(mapBlockEntity.getPos());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(MapBlockSaveMapPayload.ID, (payload, context) -> {
            if (payload.getBlockEntity(context) instanceof MapBlockEntity mapBlockEntity) {
                mapBlockEntity.saveMap(context.player());
            }
        });
    }

    private static void queues() {
        ServerPlayNetworking.registerGlobalReceiver(QueuePayload.ID, (payload, context) -> {
            GamesManager.getInstance().setPlayerQueues(context.player(), payload.queues());
        });
        ServerPlayNetworking.registerGlobalReceiver(RequestOpenQueueScreenPayload.ID, (payload, context) -> {
            final GamesManager gamesManager = GamesManager.getInstance();
            if (!gamesManager.isPlayerInGame(context.player())) {
                ServerPlayNetworking.send(context.player(), new OpenQueueScreenPayload(gamesManager.getPlayerQueues(context.player()), gamesManager.allowsMultiQueue()));
            }
        });
    }
}
