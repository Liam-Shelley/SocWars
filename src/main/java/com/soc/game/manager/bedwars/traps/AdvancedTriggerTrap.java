package com.soc.game.manager.bedwars.traps;

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
        void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor owningTeam);
    }

    public static void initialise() {}

    public static final AbstractTrap SWITCHEROO = register(new AdvancedTriggerTrap("switcheroo", Items.ENDER_PEARL.getDefaultStack(), 12 * 20, (pos, manager, enemiesInRange, owningTeam) -> {
        final List<Vec3d> positions = enemiesInRange.stream().map(ServerPlayerEntity::getPos).collect(Collectors.toList());

        Collections.shuffle(positions);
        for (final ServerPlayerEntity enemy : enemiesInRange) {
            final Vec3d position = positions.removeFirst();
            enemy.requestTeleport(position.x, position.y, position.z);
        }
    }));
    public static final AbstractTrap RETURN_TO_BASE = register(new AdvancedTriggerTrap("return_to_base", Items.RED_BED.getDefaultStack(), 8 * 20, (pos, manager, enemiesInRange, owningTeam) -> {
        manager.getSpawnPosition(owningTeam).map(BlockPos::toCenterPos).ifPresent(spawnPos -> manager.getPlayers(owningTeam).forEach(player -> player.requestTeleport(spawnPos.x, spawnPos.y, spawnPos.z)));
    }));
    public static final AbstractTrap GUERILLA_COUNTER = register(new AdvancedTriggerTrap("guerilla_counter", Items.JUNGLE_SAPLING.getDefaultStack(), 10 * 20, (pos, manager, enemiesInRange, owningTeam) -> {
        manager.getPlayers(owningTeam).forEach(player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, player.getPos().isInRange(pos, 30) ? 10 * 20 : 5 * 20, 0, false, false)));
    }));
    public static final AbstractTrap ATTACKER_SWAP = register(new AdvancedTriggerTrap("attacker_swap", Items.CHAIN.getDefaultStack(), 6 * 20, (pos, manager, enemiesInRange, owningTeam) -> {
        final List<ServerPlayerEntity> teamPlayers = new ArrayList<>(manager.getPlayers(owningTeam));
        for (ServerPlayerEntity enemy : enemiesInRange) {
            if (teamPlayers.isEmpty()) return;
            swapPositions(enemy, teamPlayers.removeFirst()); //May have to refine this to work better based on the whole
        }
    }));
    public static final AbstractTrap RAID_SIREN = register(new AdvancedTriggerTrap("raid_siren", Items.SCULK_SHRIEKER.getDefaultStack(), 10 * 20, (pos, manager, enemiesInRange, owningTeam) -> {
        manager.getWorld().playSound(null, pos.x, pos.y, pos.z, Sounds.NUCLEAR_SIREN, SoundCategory.MASTER, 10f, 1f);
        manager.broadcast(Text.translatable("game.bedwars.raid_siren_activated", colouredTextFromColour(owningTeam)), false);
    }));

    final TriggerFunction triggerFunction;

    public AdvancedTriggerTrap(String id, ItemStack icon, int time, TriggerFunction triggerFunction) {
        super(id, icon, time);
        this.triggerFunction = triggerFunction;
    }

    @Override
    public void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemies, DyeColor team, float amplifier) {
        this.triggerFunction.trigger(pos, manager, enemies, team);
    }

    private static void swapPositions(ServerPlayerEntity a, ServerPlayerEntity b) { //Could pull this to a lib class. I should probably also split up my SocwarsLib class because it's gross atm
        final Vec3d aPos = a.getPos(); //This should be fine since setPos assigns a new Vec3d to pos rather that mutating pos itself but what do I know I'm just a stupid idiot progammer
        final Vec3d bPos = b.getPos();
        a.requestTeleport(bPos.x, bPos.y, bPos.z);
        b.requestTeleport(aPos.x, aPos.y, aPos.z);
    }
}
