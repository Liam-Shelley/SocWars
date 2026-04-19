package com.soc.game.manager.bedwars.traps;

import com.soc.SocWars;
import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.bedwars.shopitems.DisplayShopItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public abstract class AbstractAbility implements Triggerable {
    public static final String KEY = "ability";

    private final Identifier id;
    private final ItemStack icon;
    private final int cooldownTime;
    private final TriggerReason triggerReason;

    public AbstractAbility(Identifier id, ItemStack icon, int time, TriggerReason triggerReason) {
        this.id = id;
        this.icon = icon;
        this.cooldownTime = time;
        this.triggerReason = triggerReason;
    }

    public AbstractAbility(String id, ItemStack icon, int time, TriggerReason triggerReason) {
        this(Identifier.of(SocWars.MOD_ID, id), icon, time, triggerReason);
    }

    protected abstract ResultModifier trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor team, AbstractTrap trapTriggerFunction);

    public final void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemiesInRange, DyeColor team, TriggerReason triggerReason, AbstractTrap trapTriggerFunction) {
        if (this.triggerReason == triggerReason) this.trigger(pos, manager, enemiesInRange, team, trapTriggerFunction);
    }

    @Override
    public final int getCooldownTime() {
        return this.cooldownTime;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public final Identifier getId() {
        return this.id;
    }

    private String getBaseName() {
        return "ability." + this.getId().getPath();
    }

    @Override
    public Text getName() {
        return Text.translatable(this.getBaseName());
    }

    public Text getTooltip() {
        return Text.translatable(this.getBaseName() + ".tooltip", Text.literal(String.valueOf(this.cooldownTime / 20)).formatted(Formatting.DARK_GREEN));
    }

    @Override
    public DisplayShopItem getDisplayShopItem() {
        return new DisplayShopItem(this.getIcon(), this.getName(), this.getTooltip());
    }

    public TriggerReason getTriggerReason() {
        return this.triggerReason;
    }

    @Override
    public boolean isAbility() {
        return true;
    }
}
