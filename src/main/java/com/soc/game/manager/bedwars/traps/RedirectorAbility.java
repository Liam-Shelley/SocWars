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

public class RedirectorAbility extends AbstractAbility {
    public interface TriggerFunction {
        boolean trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor team, TrapTriggerFunction trapTriggerFunction);
    }

    public static void initialise() {}

    public static final AbstractAbility DISGUISE = register(new RedirectorAbility("disguise", Items.OAK_LEAVES.getDefaultStack(), 15 * 20, ((pos, manager, enemiesInRange, team, trapTriggerFunction) -> {
        trapTriggerFunction.trigger(pos, manager, enemiesInRange, team);
        return false;
    })));
    public static final AbstractAbility RESISTANCE = register(new RedirectorAbility("resistance", Items.SHIELD.getDefaultStack(), 20 * 20, ((pos, manager, enemiesInRange, team, trapTriggerFunction) -> {
        trapTriggerFunction.trigger(pos, manager, List.of(), team);
        return true;
    })));
    public static final AbstractAbility UNO_REVERSE_CARD = register(new RedirectorAbility("uno_reverse_card", Items.SHIELD.getDefaultStack(), 20 * 20, ((pos, manager, enemiesInRange, team, trapTriggerFunction) -> {

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
