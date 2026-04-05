package com.soc.game.manager.bedwars.traps;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.items.BaseWeapon;
import com.soc.items.EatFunctionFood;
import com.soc.lib.Events;
import com.soc.networking.s2c.JumpscarePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.resetScale;
import static com.soc.lib.SocWarsLib.scaleEntity;
import static com.soc.game.manager.bedwars.traps.Traps.register;

public class SimpleTriggerTrap extends AbstractTrap {
    public static void initialise() {}

    public static final AbstractTrap MINING_FATIGUE = register(new SimpleTriggerTrap("mining_fatigue", Items.TRIPWIRE_HOOK.getDefaultStack(), 12 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 12 * 20, 1, false, true, true))));
    public static final AbstractTrap CLEAR_EFFECTS = register(new SimpleTriggerTrap("clear_effects", Items.MILK_BUCKET.getDefaultStack(), 5 * 20, ServerPlayerEntity::clearStatusEffects));
    public static final AbstractTrap LAUNCH = register(new SimpleTriggerTrap("launch", Items.PISTON.getDefaultStack(), 5 * 20, player -> player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), new Vec3d(0d, 2d, 0d)))));
    public static final AbstractTrap STEVIE_WONDER = register(new SimpleTriggerTrap("steve_harvey", Items.SCULK_VEIN.getDefaultStack(), 8 * 20, player -> {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 8 * 20, 1, false, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 8 * 20, 1, false, true, true));
    }));
    public static final AbstractTrap SHUFFLE = register(new SimpleTriggerTrap("shuffle", Items.STRING.getDefaultStack(), 5 * 20, player -> {
        final PlayerInventory inventory = player.getInventory();
        final List<ItemStack> items = new ArrayList<>(inventory.getMainStacks());
        Collections.shuffle(items);
        for (int i = 0; !items.isEmpty(); i++) {
            inventory.setStack(i, items.removeFirst());
        }
    }));
    public static final AbstractTrap PERPLEXITY = register(new SimpleTriggerTrap("perplexity", Items.DIAMOND_HOE.getDefaultStack(), 6 * 20, player -> player.addStatusEffect(new StatusEffectInstance(ModEffects.PERPLEXITY, 6 * 20, 0, false, true, true))));
    public static final AbstractTrap ENLARGEMENT = register(new SimpleTriggerTrap("enlargement", EatFunctionFood.BIGGENING_PILLS.getDefaultStack(), 10 * 20, player -> {
        scaleEntity(player, 2f);
        Events.getInstance().scheduleEvent(() -> resetScale(player), 20 * 20);
    }));
    public static final AbstractTrap GLOWING = register(new SimpleTriggerTrap("glowing", Items.TORCH.getDefaultStack(), 8 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 8 * 20, 0, false, true, true))));
    public static final AbstractTrap POSTURA = register(new SimpleTriggerTrap("postura", BaseWeapon.BAT.getDefaultStack(), 10 * 20, player -> player.addStatusEffect(new StatusEffectInstance(ModEffects.ARTHRODESIS, 10 * 20, 0, false, true, true))));
    public static final AbstractTrap SPEED = register(new SimpleTriggerTrap("speed", Items.BLUE_ICE.getDefaultStack(), 2 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2 * 20, 9, false, true, true))));
    public static final AbstractTrap JUMPSCARE = register(new SimpleTriggerTrap("jumpscare", Items.CREEPER_HEAD.getDefaultStack(), 2 * 20, player -> ServerPlayNetworking.send(player, new JumpscarePayload(SoundEvents.ENTITY_ENDERMAN_DEATH, Identifier.of(SocWars.MOD_ID, "")))));

    private final Consumer<ServerPlayerEntity> enemyTriggerFunction;

    public SimpleTriggerTrap(String id, ItemStack icon, int time, Consumer<ServerPlayerEntity> enemyTriggerFunction) {
        super(id, icon, time);
        this.enemyTriggerFunction = enemyTriggerFunction;
    }

    @Override
    public void trigger(Vec3d pos, List<ServerPlayerEntity> team, List<ServerPlayerEntity> enemies, World world) {
        enemies.forEach(this.enemyTriggerFunction);
    }
}
