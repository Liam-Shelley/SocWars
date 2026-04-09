package com.soc.game.manager.bedwars.traps;

import com.google.common.collect.Multimap;
import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.TrapGame;
import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.game.manager.bedwars.shopitems.SimpleShopItem;
import com.soc.lib.SocWarsLib;
import com.soc.networking.s2c.bedwars.UseTrapOrAbilityPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class TrapManager {
    public static final double TRAP_DETECTION_RANGE = 10d;
    public static final int TRAP_CAPACITY = 5;
    public static final int ABILITY_CAPACITY = 3;

    private final DyeColor teamColour;
    private final World world;

    private final Queue<AbstractTrap> traps = new LinkedList<>();
    private final Queue<AbstractAbility> abilities = new LinkedList<>();
    private long nextTrapTriggerTime;
    private int currentTrapDuration;
    private long nextAbilityTriggerTime;
    private int currentAbilityDuration;

    public TrapManager(DyeColor teamColour, World world) {
        this.teamColour = teamColour;
        this.world = world;
        this.nextTrapTriggerTime = world.getTime();
    }

    public boolean hasActiveTrap() {
        return !this.traps.isEmpty() && this.nextTrapTriggerTime < this.world.getTime();
    }

    public boolean hasActiveAbility(TriggerReason reason) {
        return !this.abilities.isEmpty() && this.abilities.peek().getTriggerReason() == reason && this.nextAbilityTriggerTime < this.world.getTime();
    }

    public void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemiesInRange, DyeColor owningTeam) {
        if (manager instanceof TrapGame trapGame) {
            final AbstractTrap trap = this.traps.remove();

            final Set<DyeColor> alertingTeams = new HashSet<>();

            enemiesInRange.keySet().forEach(enemyTeam -> {
                if (trapGame.getTrapManager(enemyTeam).receiveTrap(pos, manager, manager.getPlayers(enemyTeam), owningTeam, trap)) {
                    alertingTeams.add(enemyTeam);
                }
            });

            this.sendUsePackets(manager, alertingTeams, owningTeam, trap);

            this.nextTrapTriggerTime = this.world.getTime() + trap.getCooldownTime();
            this.currentTrapDuration = trap.getCooldownTime();
        }
    }

    public boolean receiveTrap(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor owningTeam, AbstractTrap trap) {
        if (this.hasActiveAbility(TriggerReason.TRAP_RESPONSE)) {
            return this.triggerAbility(pos, manager, enemiesInRange, owningTeam, trap);
        } else {
            trap.trigger(pos, manager, enemiesInRange, owningTeam);
            return true;
        }
    }

    public boolean triggerAbility(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor owningTeam, TrapTriggerFunction trapTriggerFunction) {
        final AbstractAbility ability = this.abilities.remove();

        this.sendUsePackets(manager, Set.of(owningTeam), this.teamColour, ability);

        this.nextAbilityTriggerTime = this.world.getTime() + ability.getCooldownTime();
        this.currentAbilityDuration = ability.getCooldownTime();

        return ability.trigger(pos, manager, enemiesInRange, owningTeam, trapTriggerFunction);
    }

    private void sendUsePackets(AbstractGameManager<?, ?, ?> manager, Set<DyeColor> alertingTeams, DyeColor owningTeam, Triggerable triggerable) {
        final String titleKey = triggerable.isAbility() ? "ability" : "trap";

        final Text teamsText = getEnemiesInRangeText(alertingTeams);
        manager.getPlayers(owningTeam).forEach(player -> {
            ServerPlayNetworking.send(player, new UseTrapOrAbilityPayload(this.world.getTime() + triggerable.getCooldownTime(), triggerable.getCooldownTime(), triggerable.isAbility()));

            if (!alertingTeams.isEmpty()) {
                player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.bedwars." + titleKey + "_triggered.title"))); //Cache these before the loop? //Yeah I really don't need to do that
                player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("game.bedwars." + titleKey + "_triggered.subtitle", triggerable.getName(), teamsText)));
            }
        });
    }

    public boolean buyTrap(AbstractTrap trap) {
        if (this.traps.size() >= TRAP_CAPACITY) return false;

        this.traps.add(trap);
        return true;
    }

    public boolean buyAbility(AbstractAbility ability) {
        if (this.abilities.size() >= ABILITY_CAPACITY) return false;

        this.abilities.add(ability);
        return true;
    }

    public int[] getTrapProgressStats() {
        return new int[]{
                (int)this.nextTrapTriggerTime,
                this.currentTrapDuration,
                (int)this.nextAbilityTriggerTime,
                this.currentAbilityDuration
        };
    }

    public BedwarsShopCategory getTrapsDisplay() {
        return this.getDisplayFromList(this.traps, TRAP_CAPACITY);
    }

    public BedwarsShopCategory getAbilitiesDisplay() {
        return this.getDisplayFromList(this.abilities, ABILITY_CAPACITY);
    }

    private BedwarsShopCategory getDisplayFromList(Collection<? extends Triggerable> list, int padSize) {
        final List<ShopItem<?>> displayItems = list.stream().map(Triggerable::getDisplayShopItem).collect(Collectors.toList());

        while (displayItems.size() < padSize) displayItems.add(SimpleShopItem.EMPTY);

        return new BedwarsShopCategory(displayItems);
    }

    private static Text getEnemiesInRangeText(Set<DyeColor> alertingTeams) {
        return alertingTeams.stream().map(SocWarsLib::colouredTextFromColour).reduce((a, b) -> a.append(", ").append(b)).orElse(Text.empty()); //Probably just make this return an optional
    }
}
