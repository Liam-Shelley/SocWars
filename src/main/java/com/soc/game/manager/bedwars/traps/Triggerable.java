package com.soc.game.manager.bedwars.traps;

import com.soc.game.manager.bedwars.shopitems.DisplayShopItem;
import net.minecraft.text.Text;

public interface Triggerable {
    int getCooldownTime();

    Text getName();

    boolean isAbility();

    DisplayShopItem getDisplayShopItem();
}
