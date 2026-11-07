package com.soc.items.util;

import com.soc.items.*;
import com.soc.itemsrework.SocWarsBowItem;

public interface ModItems {
    static void initialise() {
        ModItemGroups.initialise();

        HardCodes.initialise();


        DrawableWeapon.initialise();
        SocWarsBowItem.initialise();
    }
}