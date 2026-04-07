package com.soc.game.manager.bedwars.traps;

import com.google.common.collect.Multimap;
import com.soc.game.manager.AbstractGameManager;
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
    public static final double TRAP_DETECTION_RANGE = 8d;
    public static final int TRAP_CAPACITY = 5;
    public static final int ABILITY_CAPACITY = 3;

    private final Set<UUID> team;
    private final World world;

    private final Queue<AbstractTrap> traps = new LinkedList<>();
    private final Queue<AbstractAbility> abilities = new LinkedList<>();
    private long nextTrapTriggerTime;
    private int currentTrapDuration;
    private long nextAbilityTriggerTime;
    private int currentAbilityDuration;

    public TrapManager(Set<UUID> players, World world) {
        this.team = players;
        this.world = world;
        this.nextTrapTriggerTime = world.getTime();
    }

    public boolean hasActiveTrap() {
        return !this.traps.isEmpty() && this.nextTrapTriggerTime < this.world.getTime();
    }

    public boolean hasActiveAbility(TriggerReason reason) {
        return !this.abilities.isEmpty() && this.abilities.peek().getTriggerReason() == reason && this.nextAbilityTriggerTime < this.world.getTime();
    }

    public void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemiesInRange, DyeColor team) { //Eventually consolidate the two methods
        final AbstractTrap trap = this.traps.remove();

        if (this.hasActiveAbility(TriggerReason.TRAP_RESPONSE)) {
            this.triggerAbility(pos, manager, enemiesInRange, team, trap); //I probably need to return something to handle whether I actually send packets and whatnot
        } else {
            trap.trigger(pos, manager, enemiesInRange, team);
            this.sendUsePackets(manager, enemiesInRange, team, trap);

            this.nextTrapTriggerTime = this.world.getTime() + trap.getCooldownTime();
            this.currentTrapDuration = trap.getCooldownTime();
        }
    }

    public void triggerAbility(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemiesInRange, DyeColor team, TrapTriggerFunction trapTriggerFunction) {
        final AbstractAbility ability = this.abilities.remove();

        ability.trigger(pos, manager, enemiesInRange, team, this.traps.poll());
        this.sendUsePackets(manager, enemiesInRange, team, ability);

        this.nextAbilityTriggerTime = this.world.getTime() + ability.getCooldownTime();
        this.currentAbilityDuration = ability.getCooldownTime();
    }

    private void sendUsePackets(AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemiesInRange, DyeColor team, Triggerable triggerable) {
        final String titleKey = triggerable.isAbility() ? "ability" : "trap";

        final Text teamsText = getEnemiesInRangeText(enemiesInRange);
        manager.getPlayers(team).forEach(player -> {
            ServerPlayNetworking.send(player, new UseTrapOrAbilityPayload(this.world.getTime() + triggerable.getCooldownTime(), triggerable.getCooldownTime(), triggerable.isAbility()));

            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.bedwars." + titleKey + "_triggered.title"))); //Cache these before the loop? //Yeah I really don't need to do that
            player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("game.bedwars." + titleKey + "_triggered.subtitle", triggerable.getName(), teamsText)));
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

    private static Text getEnemiesInRangeText(Multimap<DyeColor, ServerPlayerEntity> enemiesInRange) {
        return enemiesInRange.keySet().stream().distinct().map(SocWarsLib::colouredTextFromColour).reduce((a, b) -> a.append(", ").append(b)).get();
    }
}
