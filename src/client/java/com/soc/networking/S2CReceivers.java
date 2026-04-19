package com.soc.networking;

import com.soc.game.BedwarsTeamsHUD;
import com.soc.game.BlockProtectionManagerAndHud;
import com.soc.game.EventsHud;
import com.soc.lib.Coroutine;
import com.soc.lib.Coroutines;
import com.soc.lib.Events;
import com.soc.mixin.client.GetOptionsVolumes;
import com.soc.networking.s2c.*;
import com.soc.networking.s2c.bedwars.*;
import com.soc.player.PlayerData;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.lib.SocWarsLib.iterateInSphere;
import static com.soc.lib.SocWarsLib.randomCentredVec3d;

public class S2CReceivers {
    public static void initialise() {
        ClientPlayNetworking.registerGlobalReceiver(JoinQueuePayload.ID, (payload, context) -> {
                context.player().sendMessage(Text.translatable("queue.join", payload.queue()), false);

                //Some horrific stuff I was testing
                //final Object2IntMap<BlockState> blockStateMap = ((GetLoadedModelGroups)MinecraftClient.getInstance().getBakedModelManager()).getModelGroups();
                //final int greyWoolState = blockStateMap.getInt(Blocks.GRAY_BANNER.getDefaultState());
                //blockStateMap.put(Blocks.RED_BANNER.getDefaultState(), greyWoolState);
        });
        ClientPlayNetworking.registerGlobalReceiver(LeaveQueuePayload.ID, (payload, context) -> {
                context.player().sendMessage(Text.translatable("queue.leave", payload.queue()), false);
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDataPayload.ID, (payload, context) -> {
                if (context.player() == MinecraftClient.getInstance().player) PlayerData.CLIENT_INSTANCE = payload.playerData();
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
        ClientPlayNetworking.registerGlobalReceiver(BedwarsIndividualShopDataPayload.ID, (payload, context) -> {
            final ScreenHandler screenHandler = context.player().currentScreenHandler;
            if (screenHandler.syncId == payload.syncId() && screenHandler instanceof BedwarsIndividualShopScreenHandler shopScreenHandler) {
                shopScreenHandler.setShopContents(payload.shopContents());
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(BedwarsTeamShopDataPayload.ID, (payload, context) -> {
            final ScreenHandler screenHandler = context.player().currentScreenHandler;
            if (screenHandler.syncId == payload.syncId() && screenHandler instanceof BedwarsTeamShopScreenHandler shopScreenHandler) {
                shopScreenHandler.setShopContents(payload.shopContents());
                shopScreenHandler.setTrapProgress(payload.trapProgressStats());
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
        ClientPlayNetworking.registerGlobalReceiver(UseTrapOrAbilityPayload.ID, (payload, context) -> {
            final ScreenHandler screenHandler = context.player().currentScreenHandler;
            if (screenHandler instanceof BedwarsTeamShopScreenHandler shopScreenHandler) {
                if (payload.isAbility()) {
                    shopScreenHandler.useAbility(payload.nextTime(), payload.duration());
                } else {
                    shopScreenHandler.useTrap(payload.nextTime(), payload.duration());
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SmokescreenPayload.ID, (payload, context) -> {
            final World world = context.player().getWorld();
            final int randomOffset = world.random.nextInt();

            final AtomicInteger count = new AtomicInteger(0);
            Coroutines.getInstance().startCoroutine(new Coroutine<>(count, t -> {
                final int currentTime = t.getAndIncrement();

                if (currentTime % 5 == 0) {
                    final Random random = new LocalRandom(currentTime + randomOffset);
                    context.player().playSound(SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, 1f, currentTime / 200f + 1.1f);

                    iterateInSphere(payload.pos(), 5f, 0.5f, currentPos -> {
                        final Vec3d randomPosOffset = randomCentredVec3d(random).multiply(0.5d);

                        final double x = currentPos.getX() + randomPosOffset.x + 0.5d; //How about I don't forget to centre it from the blockpos
                        final double y = currentPos.getY() + randomPosOffset.y + 0.5d;
                        final double z = currentPos.getZ() + randomPosOffset.z + 0.5d;

                        world.addParticleClient(ParticleTypes.CLOUD, true, true, x, y, z, random.nextFloat() * 0.05f - 0.025f, random.nextFloat() * 0.05f - 0.025f, random.nextFloat() * 0.05f - 0.025f);
                    });
                }

                return currentTime > 80;
            }));
        });
        ClientPlayNetworking.registerGlobalReceiver(BatchParticlePayload.ID, ((payload, context) -> {
            final World world = context.player().getWorld();
            final Vec3d velocity = payload.velocity();
            payload.positions().forEach(pos -> world.addParticleClient(payload.particleType(), pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z));
        }));
        ClientPlayNetworking.registerGlobalReceiver(BlockProtectionPayload.ID, ((payload, context) -> {
            BlockProtectionManagerAndHud.INSTANCE.setBlockProtection(payload);
        }));
        ClientPlayNetworking.registerGlobalReceiver(JumpscarePayload.ID, ((payload, context) -> {
             context.player().sendMessage(Text.of("Boo!"), false);

            //TODO: Write the jumpscare code
        }));
        ClientPlayNetworking.registerGlobalReceiver(SilencePayload.ID, ((payload, context) -> {
            final SimpleOption<Double> masterVolume = ((GetOptionsVolumes)MinecraftClient.getInstance().options).getSoundVolumeLevels().get(SoundCategory.MASTER);
            final Double startingVolume = masterVolume.getValue();
            if (startingVolume < 10e-5d) return;

            masterVolume.setValue(0d);
            Events.getInstance().scheduleEvent(() -> {
                if (masterVolume.getValue() < 10e-5d) masterVolume.setValue(startingVolume);
            }, payload.time());
        }));
        ClientPlayNetworking.registerGlobalReceiver(LeaveGamePayload.ID, ((payload, context) -> {
            BlockProtectionManagerAndHud.INSTANCE.clearBlockProtection();
            EventsHud.clear();
        }));
        ClientPlayNetworking.registerGlobalReceiver(SetAnglesPayload.ID, ((payload, context) -> {
            final PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.getId() == payload.entityId()) {
                player.setAngles(payload.yaw(), payload.pitch());
            }
        }));
        ClientPlayNetworking.registerGlobalReceiver(EventQueuePayload.ID, ((payload, context) -> {
            EventsHud.receivePayload(payload);
        }));
    }
}
