package com.soc.game.manager.bedwars.traps;

import com.google.common.collect.Multimap;
import com.soc.game.manager.AbstractGameManager;
import com.soc.util.Sounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.stream.Collectors;

import static com.soc.game.manager.bedwars.traps.Traps.register;
import static com.soc.lib.SocWarsLib.colouredTextFromColour;

public class AdvancedTriggerTrap extends AbstractTrap {
    public interface TriggerFunction {
        void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemies, DyeColor team);
    }

    public static void initialise() {}

    public static final AbstractTrap SWITCHEROO = register(new AdvancedTriggerTrap("switcheroo", Items.ENDER_PEARL.getDefaultStack(), 12 * 20, (pos, manager, enemies, team) -> {
        enemies.keySet().forEach(enemyTeam -> {
            final Collection<ServerPlayerEntity> enemyPlayers = manager.getPlayers(enemyTeam);
            final List<Vec3d> positions = enemyPlayers.stream().map(ServerPlayerEntity::getPos).collect(Collectors.toList());

            Collections.shuffle(positions);
            for (final ServerPlayerEntity enemy : enemyPlayers) {
                final Vec3d position = positions.removeFirst();
                enemy.requestTeleport(position.x, position.y, position.z);
            }
        });
    }));
    public static final AbstractTrap RETURN_TO_BASE = register(new AdvancedTriggerTrap("return_to_base", Items.RED_BED.getDefaultStack(), 8 * 20, (pos, manager, enemies, team) -> {
        manager.getSpawnPosition(team).map(BlockPos::toCenterPos).ifPresent(spawnPos -> manager.getPlayers(team).forEach(player -> player.requestTeleport(spawnPos.x, spawnPos.y, spawnPos.z)));
    }));
    public static final AbstractTrap GUERILLA_COUNTER = register(new AdvancedTriggerTrap("guerilla_counter", Items.JUNGLE_SAPLING.getDefaultStack(), 10 * 20, (pos, manager, enemies, team) -> {
        manager.getPlayers(team).forEach(player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, player.getPos().isInRange(pos, 30) ? 10 * 20 : 5 * 20, 0, false, false)));
    }));
    public static final AbstractTrap ATTACKER_SWAP = register(new AdvancedTriggerTrap("attacker_swap", Items.CHAIN.getDefaultStack(), 6 * 20, (pos, manager, enemies, team) -> {
        final List<ServerPlayerEntity> teamPlayers = new ArrayList<>(manager.getPlayers(team));
        for (ServerPlayerEntity enemy : enemies.values()) {
            if (teamPlayers.isEmpty()) return;
            swapPositions(enemy, teamPlayers.removeFirst());
        }
    }));
    public static final AbstractTrap RAID_SIREN = register(new AdvancedTriggerTrap("raid_siren", Items.SCULK_SHRIEKER.getDefaultStack(), 10 * 20, (pos, manager, enemies, team) -> {
        manager.getWorld().playSound(null, pos.x, pos.y, pos.z, Sounds.NUCLEAR_SIREN, SoundCategory.MASTER, 10f, 1f);
        manager.broadcast(Text.translatable("game.bedwars.raid_siren_activated", colouredTextFromColour(team)), false);
    }));

    final TriggerFunction triggerFunction;

    public AdvancedTriggerTrap(String id, ItemStack icon, int time, TriggerFunction triggerFunction) {
        super(id, icon, time);
        this.triggerFunction = triggerFunction;
    }

    @Override
    public void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemies, DyeColor team) {
        this.triggerFunction.trigger(pos, manager, enemies, team);
    }

    private static void swapPositions(ServerPlayerEntity a, ServerPlayerEntity b) { //Could pull this to a lib class. I should probably also split up my SocwarsLib class because it's gross atm
        final Vec3d aPos = a.getPos(); //This should be fine since setPos assigns a new Vec3d to pos rather that mutating pos itself but what do I know I'm just a stupid idiot progammer
        final Vec3d bPos = b.getPos();
        a.requestTeleport(bPos.x, bPos.y, bPos.z);
        b.requestTeleport(aPos.x, aPos.y, aPos.z);
    }
}
