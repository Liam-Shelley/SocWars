package com.soc.items.util;

import com.soc.items.*;

public interface ModItems {
    static void initialise() {
        ModItemGroups.initialise();

        HardCodes.initialise();

        DrawableWeapon.initialise();
        BowItem.initialise();

        GamblerSword.initialise();

        GamblerArmour.initialise();
        SteadfastArmour.initialise();
        CartoonArmour.initialise();
        GlassArmour.initialise();
        DemolitionistArmour.initialise();
        IllusionerArmour.initialise();
        TrainingWeights.initialise();
        SummonersGarb.initialise();
    }
}