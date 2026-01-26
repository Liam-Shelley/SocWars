package com.soc.game.manager.bedwars.traps;

import com.soc.effects.util.ModEffects;
import com.soc.items.BaseWeapon;
import com.soc.items.EatFunctionFood;
import com.soc.lib.Events;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.resetScale;
import static com.soc.lib.SocWarsLib.scaleEntity;
import static com.soc.game.manager.bedwars.traps.Traps.register;

public class SimpleTriggerTrap extends Trap {
    public static void initialise() {}

    public static final Trap MINING_FATIGUE = register(new SimpleTriggerTrap("mining_fatigue", Items.TRIPWIRE_HOOK.getDefaultStack(), 20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20 * 20, 1, false, true, true))));
    public static final Trap CLEAR_EFFECTS = register(new SimpleTriggerTrap("clear_effects", Items.MILK_BUCKET.getDefaultStack(), 20 * 20, ServerPlayerEntity::clearStatusEffects));
    public static final Trap LAUNCH = register(new SimpleTriggerTrap("launch", Items.PISTON.getDefaultStack(), 5 * 20, player -> player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), new Vec3d(0d, 2d, 0d)))));
    public static final Trap STEVIE_WONDER = register(new SimpleTriggerTrap("steve_harvey", Items.SCULK_VEIN.getDefaultStack(), 20 * 20, player -> {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20 * 20, 1, false, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 20, 1, false, true, true));
    }));
    public static final Trap SHUFFLE = register(new SimpleTriggerTrap("shuffle", Items.STRING.getDefaultStack(), 5 * 20, player -> {
        final PlayerInventory inventory = player.getInventory();
        final List<ItemStack> items = new ArrayList<>(inventory.getMainStacks());
        Collections.shuffle(items);
        for (int i = 0; !items.isEmpty(); i++) {
            inventory.setStack(i, items.removeFirst());
        }
    }));
    public static final Trap PERPLEXITY = register(new SimpleTriggerTrap("perplexity", Items.DIAMOND_HOE.getDefaultStack(), 20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(ModEffects.PERPLEXITY, 20 * 20, 0, false, true, true))));
    public static final Trap ENLARGEMENT = register(new SimpleTriggerTrap("enlargement", EatFunctionFood.BIGGENING_PILLS.getDefaultStack(), 20 * 20, player -> {
        scaleEntity(player, 2f);
        Events.getInstance().scheduleEvent(() -> resetScale(player), 20 * 20);
    }));
    public static final Trap GLOWING = register(new SimpleTriggerTrap("glowing", Items.TORCH.getDefaultStack(), 20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 20, 0, false, true, true))));
    public static final Trap POSTURA = register(new SimpleTriggerTrap("postura", BaseWeapon.BAT.getDefaultStack(), 20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(ModEffects.ARTHRODESIS, 20 * 20, 0, false, true, true))));
    public static final Trap SPEED = register(new SimpleTriggerTrap("speed", Items.BLUE_ICE.getDefaultStack(), 2 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2 * 20, 9, false, true, true))));

    private final Consumer<ServerPlayerEntity> enemyTriggerFunction;

    public SimpleTriggerTrap(String id, ItemStack icon, int time, Consumer<ServerPlayerEntity> enemyTriggerFunction) {
        super(id, icon, time);
        this.enemyTriggerFunction = enemyTriggerFunction;
    }

    @Override
    public void trigger(Vec3d pos, List<ServerPlayerEntity> team, List<ServerPlayerEntity> enemies) {
        enemies.forEach(this.enemyTriggerFunction);
    }
}
