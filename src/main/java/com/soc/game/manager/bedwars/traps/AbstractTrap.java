package com.soc.game.manager.bedwars.traps;

import com.soc.SocWars;
import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.bedwars.shopitems.DisplayShopItem;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public abstract class AbstractTrap implements Triggerable, TrapTriggerFunction {
    public static final String KEY = "trap";

    private final Identifier id;
    private final ItemStack icon;
    private int cooldownTime;

    public AbstractTrap(Identifier id, ItemStack icon, int time) {
        this.id = id;
        this.icon = icon;
        this.cooldownTime = time;
    }

    public AbstractTrap(String id, ItemStack icon, int time) {
        this(Identifier.of(SocWars.MOD_ID, id), icon, time);
    }

    @Override
    public abstract void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemies, DyeColor team);

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
        return "trap." + this.getId().getPath();
    }

    @Override
    public Text getName() {
        return Text.translatable(this.getBaseName());
    }

    public Text getTooltip() {
        return Text.translatable(this.getBaseName() + ".tooltip", Text.literal(String.valueOf(this.cooldownTime / 20)).formatted(Formatting.DARK_GREEN)); //this is a little gross but eh it saves me from having to remember to update the tooltip duration when I change trap durations
    }

    @Override
    public DisplayShopItem getDisplayShopItem() {
        return new DisplayShopItem(this.getIcon(), this.getName(), this.getTooltip());
    }

    @Override
    public boolean isAbility() {
        return false;
    }

    public void modifyCooldownTime(Int2IntFunction modifier) {
        this.cooldownTime = modifier.applyAsInt(this.cooldownTime);
    }
}
