package com.soc.game.manager.bedwars.traps;

import com.google.common.collect.Multimap;
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

public abstract class AbstractTrap {
    public enum TriggerReason {
        
    }

    public static final String KEY = "trap";

    private final Identifier id;
    private final ItemStack icon;
    private final int cooldownTime;

    public AbstractTrap(Identifier id, ItemStack icon, int time) {
        this.id = id;
        this.icon = icon;
        this.cooldownTime = time;
    }

    public AbstractTrap(String id, ItemStack icon, int time) {
        this(Identifier.of(SocWars.MOD_ID, id), icon, time);
    }

    public abstract void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemies, DyeColor team);

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

    public Text getName() {
        return Text.translatable(this.getBaseName());
    }

    public Text getTooltip() {
        return Text.translatable(this.getBaseName() + ".tooltip", Text.literal(String.valueOf(this.cooldownTime / 20)).formatted(Formatting.DARK_GREEN)); //this is a little gross but eh it saves me from having to remember to update the tooltip duration when I change trap durations
    }

    public DisplayShopItem getDisplayShopItem() {
        return new DisplayShopItem(this.getIcon(), this.getName(), this.getTooltip());
    }
}
