package com.soc.game.manager.bedwars.traps;

import com.google.common.collect.Multimap;
import com.soc.game.manager.AbstractGameManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

import static com.soc.game.manager.bedwars.traps.Abilities.register;

public class RedirectorAbility extends AbstractAbility {
    public static void initialise() {}

    public static final AbstractAbility DISGUISE = register(new RedirectorAbility("lightweight", Items.FEATHER.getDefaultStack(), 8 * 20)); //Trigger as normal no alert

    public RedirectorAbility(String id, ItemStack icon, int time) {
        super(id, icon, time, TriggerReason.TRAP_RESPONSE);
    }

    @Override
    protected boolean trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemies, DyeColor team, TrapTriggerFunction trapTriggerFunction) {
        return true;
    }
}
