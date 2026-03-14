package com.soc.game.manager.bedwars.traps;

import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.game.manager.bedwars.shopitems.SimpleShopItem;
import com.soc.lib.SocWarsLib;
import com.soc.networking.s2c.bedwars.UseTrapPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

import static com.soc.game.manager.AbstractGameManager.mapUuidsToPlayers;
import static com.soc.screenhandler.BedwarsTeamShopScreenHandler.DISPLAY_SIZE;

public class TrapManager {
    public static final double TRAP_DETECTION_RANGE = 8d;

    private final Set<UUID> team;
    private final World world;

    private final Queue<Trap> traps = new LinkedList<>();
    private final Queue<Trap> abilities = new LinkedList<>();
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

    public void trigger(AbstractGameManager<?, ?, ?> manager, Vec3d pos, List<ServerPlayerEntity> players) {
        final Trap trap = this.traps.remove();
        final List<ServerPlayerEntity> team = mapUuidsToPlayers(this.world, this.team);

        trap.trigger(pos, team, players);
        team.forEach(player -> ServerPlayNetworking.send(player, new UseTrapPayload()));

        final Text teamsText = players.stream().map(manager::getTeam).distinct().map(SocWarsLib::colouredTextFromColour).reduce((a, b) -> a.append(", ").append(b)).get();
        mapUuidsToPlayers(this.world, this.team).forEach(player -> {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.bedwars.trap_triggered.title")));
            player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("game.bedwars.trap_triggered.subtitle", trap.getName(), teamsText)));
        });

        this.nextTrapTriggerTime = this.world.getTime() + trap.getCooldownTime();
        this.currentTrapDuration = trap.getCooldownTime();
    }

    private void triggerAbility(Vec3d pos, List<ServerPlayerEntity> players) {
        final Trap ability = this.traps.remove();
        final List<ServerPlayerEntity> team = mapUuidsToPlayers(this.world, this.team);

        ability.trigger(pos, team, players);
        //team.forEach(player -> ServerPlayNetworking.send(player, new UseAbilityPayload()));

        this.nextAbilityTriggerTime = this.world.getTime() + ability.getCooldownTime();
        this.currentAbilityDuration = ability.getCooldownTime();
    }

    public boolean buyTrap(Trap trap) {
        if (this.traps.size() >= DISPLAY_SIZE) return false;

        this.traps.add(trap);
        return true;
    }

    public boolean buyAbility(Trap trap) {
        if (this.abilities.size() >= DISPLAY_SIZE) return false;

        this.abilities.add(trap);
        return true;
    }

    public int[] getTrapProgressStats() {
        return new int[]{
                (int)this.nextTrapTriggerTime,
                this.currentTrapDuration,
                (int)this.nextAbilityTriggerTime,
                this.currentTrapDuration
        };
    }

    public BedwarsShopCategory getTrapsDisplay() {
        return this.getDisplayFromList(this.traps, DISPLAY_SIZE);
    }

    public BedwarsShopCategory getAbilitiesDisplay() {
        return this.getDisplayFromList(this.abilities, DISPLAY_SIZE);
    }

    private BedwarsShopCategory getDisplayFromList(Collection<Trap> list, int padSize) {
        final List<ShopItem<?>> displayItems = list.stream().map(Trap::getDisplayShopItem).collect(Collectors.toList());

        while (displayItems.size() < padSize) displayItems.add(SimpleShopItem.EMPTY);

        return new BedwarsShopCategory(displayItems);
    }
}
