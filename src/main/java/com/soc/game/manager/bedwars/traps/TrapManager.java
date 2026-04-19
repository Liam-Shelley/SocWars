package com.soc.game.manager.bedwars.traps;

import com.google.common.collect.Multimap;
import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.TrapGame;
import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.game.manager.bedwars.shopitems.SimpleShopItem;
import com.soc.lib.SocWarsLib;
import com.soc.networking.helper.TrapProgressStats;
import com.soc.networking.s2c.bedwars.UseTrapOrAbilityPayload;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
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

            final ResultModifier modifierResult = this.hasActiveAbility(TriggerReason.TRAP_MODIFIER) ? this.triggerAbility(pos, manager, enemiesInRange.values(), owningTeam, trap) : new ResultModifier(true, 1f);

            final Set<DyeColor> alertingTeams = new HashSet<>();
            enemiesInRange.keySet().forEach(enemyTeam -> {
                final ResultModifier resultModifier = trapGame.getTrapManager(enemyTeam).receiveTrap(pos, manager, manager.getPlayers(enemyTeam), owningTeam, trap, modifierResult);
                if (resultModifier.alert()) {
                    alertingTeams.add(enemyTeam);
                }
            });

            this.sendUsePackets(manager, alertingTeams, owningTeam, trap, modifierResult);

            final int modifiedDuration = (int)(trap.getCooldownTime() * modifierResult.amplifier());
            this.nextTrapTriggerTime = this.world.getTime() + modifiedDuration;
            this.currentTrapDuration = modifiedDuration;

            manager.getPlayers(owningTeam).forEach(player -> {
                if (player.currentScreenHandler instanceof BedwarsTeamShopScreenHandler handler) {
                    handler.useTrap(this.nextTrapTriggerTime, this.currentTrapDuration);
                }
            });
        }
    }

    public ResultModifier receiveTrap(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor owningTeam, AbstractTrap trap, ResultModifier resultModifier) {
        if (this.hasActiveAbility(TriggerReason.TRAP_RESPONSE)) {
            return this.triggerAbility(pos, manager, enemiesInRange, owningTeam, trap);
        } else {
            trap.trigger(pos, manager, enemiesInRange, owningTeam, resultModifier.amplifier());
            return new ResultModifier(true, 1f);
        }
    }

    public ResultModifier triggerAbility(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor owningTeam, AbstractTrap trapTriggerFunction) {
        final AbstractAbility ability = this.abilities.remove();

        this.sendUsePackets(manager, Set.of(owningTeam), this.teamColour, ability, new ResultModifier(true, 1f));

        this.nextAbilityTriggerTime = this.world.getTime() + ability.getCooldownTime();
        this.currentAbilityDuration = ability.getCooldownTime();

        manager.getPlayers(owningTeam).forEach(player -> {
            if (player.currentScreenHandler instanceof BedwarsTeamShopScreenHandler handler) {
                handler.useTrap(this.nextAbilityTriggerTime, this.currentAbilityDuration);
            }
        });

        return ability.trigger(pos, manager, enemiesInRange, owningTeam, trapTriggerFunction);
    }

    private void sendUsePackets(AbstractGameManager<?, ?, ?> manager, Set<DyeColor> alertingTeams, DyeColor owningTeam, Triggerable triggerable, ResultModifier resultModifier) {
        final String titleKey = triggerable.isAbility() ? "ability" : "trap";
        int modifiedDuration = (int)(triggerable.getCooldownTime() * resultModifier.amplifier());

        final Text teamsText = getEnemiesInRangeText(alertingTeams);
        manager.getPlayers(owningTeam).forEach(player -> {
            ServerPlayNetworking.send(player, new UseTrapOrAbilityPayload(this.world.getTime() + modifiedDuration, modifiedDuration, triggerable.isAbility()));

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

    public TrapProgressStats getTrapProgressStats() {
        return new TrapProgressStats(
                this.nextTrapTriggerTime,
                this.currentTrapDuration,
                this.nextAbilityTriggerTime,
                this.currentAbilityDuration
        );
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
        return alertingTeams.stream().map(SocWarsLib::colouredTextFromColour).reduce((a, b) -> a.append(", ").append(b)).orElse(Text.empty()); //Probably just let this return as an optional
    }

    public boolean onPlayerDeath(ServerPlayerEntity player, AbstractGameManager<?, ?, ?> manager) {
        if (!this.hasActiveAbility(TriggerReason.PLAYER_DEATH)) return true;

        return this.triggerAbility(null, manager, List.of(player), this.teamColour, null).alert();
    }
}
