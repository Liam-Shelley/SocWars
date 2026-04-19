package com.soc.game.manager.bedwars.traps;

import com.soc.game.manager.AbstractGameManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

import static com.soc.game.manager.bedwars.traps.Abilities.register;

public class PlayerDeathAbility extends AbstractAbility {
    public interface TriggerFunction {
        ResultModifier trigger(ServerPlayerEntity player, AbstractGameManager<?, ?, ?> manager, DyeColor owningTeam);
    }

    public static void initialise() {}

    public static final AbstractAbility LIFE_INSURANCE = register(new PlayerDeathAbility("life_insurance", Items.DIAMOND.getDefaultStack(), 10 * 20, (player, manager, owningTeam) -> {
        final Vec3d spawnPos = manager.getSpawnPosition(player).toCenterPos();

        player.fallDistance = 0d;
        player.requestTeleport(spawnPos.x, spawnPos.y, spawnPos.z);
        player.setHealth(10f);

        return new ResultModifier(false, 1f);
    }));

    private final TriggerFunction triggerFunction;

    public PlayerDeathAbility(String id, ItemStack icon, int time, TriggerFunction triggerFunction) {
        super(id, icon, time, TriggerReason.PLAYER_DEATH);
        this.triggerFunction = triggerFunction;
    }

    @Override
    protected ResultModifier trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor team, AbstractTrap trapTriggerFunction) {
        return this.triggerFunction.trigger(enemiesInRange.iterator().next(), manager, team);
    }
}
