package com.soc.game.manager.bedwars.traps;

import com.soc.game.manager.AbstractGameManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;

import static com.soc.game.manager.bedwars.traps.Abilities.register;
import static com.soc.game.manager.bedwars.traps.TrapManager.TRAP_DETECTION_RANGE;

public class RedirectorAbility extends AbstractAbility {
    public interface TriggerFunction {
        boolean trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor owningTeam, TrapTriggerFunction trapTriggerFunction);
    }

    public static void initialise() {}

    public static final AbstractAbility DISGUISE = register(new RedirectorAbility("disguise", Items.OAK_LEAVES.getDefaultStack(), 15 * 20, ((pos, manager, enemiesInRange, owningTeam, trapTriggerFunction) -> {
        trapTriggerFunction.trigger(pos, manager, enemiesInRange, owningTeam);
        return false;
    })));
    public static final AbstractAbility RESISTANCE = register(new RedirectorAbility("resistance", Items.SHIELD.getDefaultStack(), 20 * 20, ((pos, manager, enemiesInRange, owningTeam, trapTriggerFunction) -> {
        trapTriggerFunction.trigger(pos, manager, List.of(), owningTeam);
        return true;
    })));
    public static final AbstractAbility UNO_REVERSE_CARD = register(new RedirectorAbility("uno_reverse_card", Items.YELLOW_BANNER.getDefaultStack(), 30 * 20, ((pos, manager, enemiesInRange, owningTeam, trapTriggerFunction) -> {
        trapTriggerFunction.trigger(pos, manager, manager.getPlayers(owningTeam, player -> player.getPos().isInRange(pos, TRAP_DETECTION_RANGE * 5)), owningTeam);
        return true;
    })));

    private final TriggerFunction triggerFunction;

    public RedirectorAbility(String id, ItemStack icon, int time, TriggerFunction triggerFunction) {
        super(id, icon, time, TriggerReason.TRAP_RESPONSE);
        this.triggerFunction = triggerFunction;
    }

    @Override
    protected boolean trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor team, TrapTriggerFunction trapTriggerFunction) {
        return this.triggerFunction.trigger(pos, manager, enemiesInRange, team, trapTriggerFunction);
    }
}
