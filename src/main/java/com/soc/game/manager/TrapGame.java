package com.soc.game.manager;

import com.soc.game.manager.bedwars.traps.TrapManager;
import net.minecraft.util.DyeColor;

public interface TrapGame {
    TrapManager getTrapManager(DyeColor team);
}
