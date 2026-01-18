package com.soc.game.manager.bedwars.traps;

import com.soc.effects.util.ModEffects;
import com.soc.lib.Events;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.resetScale;
import static com.soc.lib.SocWarsLib.scaleEntity;
import static com.soc.game.manager.bedwars.traps.Traps.register;

public class SimpleTriggerTrap extends Trap {
    public static void initialise() {}

    public static final Trap MINING_FATIGUE = register("mining_fatigue", new SimpleTriggerTrap(20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20 * 20, 1, false, true, true))));
    public static final Trap CLEAR_EFFECTS = register("clear_effects", new SimpleTriggerTrap(20 * 20, ServerPlayerEntity::clearStatusEffects));
    public static final Trap LAUNCH = register("launch", new SimpleTriggerTrap(5 * 20, player -> player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), new Vec3d(0d, 2d, 0d)))));
    public static final Trap STEVIE_WONDER = register("steve_harvey", new SimpleTriggerTrap(20 * 20, player -> {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20 * 20, 1, false, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 20, 1, false, true, true));
    }));
    public static final Trap SHUFFLE = register("shuffle", new SimpleTriggerTrap(20 * 20, player -> {
        final PlayerInventory inventory = player.getInventory();
        final List<ItemStack> items = inventory.getMainStacks();
        Collections.shuffle(items);
        for (int i = 0; i < items.size(); i++) {
            inventory.setStack(i, items.removeFirst());
        }
    }));
    public static final Trap PERPLEXITY = register("perplexity", new SimpleTriggerTrap(20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(ModEffects.PERPLEXITY, 20 * 20, 1, false, true, true))));
    public static final Trap ENLARGEMENT = register("enlargement", new SimpleTriggerTrap(20 * 20, player -> {
        scaleEntity(player, 2f);
        Events.getInstance().scheduleEvent(() -> resetScale(player), 20 * 20);
    }));
    public static final Trap GLOWING = register("glowing", new SimpleTriggerTrap(20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 20, 0, false, true, true))));
    public static final Trap POSTURA = register("postura", new SimpleTriggerTrap(20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(ModEffects.ARTHRODESIS, 20 * 20, 0, false, true, true))));

    private final Consumer<ServerPlayerEntity> enemyTriggerFunction;
    private final BiConsumer<Vec3d, ServerPlayerEntity> teamPlayerTriggerFunction;

    public SimpleTriggerTrap(int time, Consumer<ServerPlayerEntity> enemyTriggerFunction, BiConsumer<Vec3d, ServerPlayerEntity> teamPlayerTriggerFunction) {
        super(time);
        this.enemyTriggerFunction = enemyTriggerFunction;
        this.teamPlayerTriggerFunction = teamPlayerTriggerFunction;
    }

    public SimpleTriggerTrap(int time, Consumer<ServerPlayerEntity> enemyTriggerFunction) {
        this(time, enemyTriggerFunction, (pos, player) -> {});
    }

    @Override
    public void trigger(Vec3d pos, List<ServerPlayerEntity> team, List<ServerPlayerEntity> players) {
        players.forEach(this.enemyTriggerFunction);
        team.forEach(player -> this.teamPlayerTriggerFunction.accept(pos, player));
    }
}
