package com.soc.items.util;

import com.soc.items.*;

public interface ModItems {
    static void initialise() {
        ModItemGroups.initialise();

        HardCodes.initialise();



        DrawableWeapon.initialise();
        BowItem.initialise();

        GamblerArmour.initialise();
        CartoonArmour.initialise();
        GlassArmour.initialise();
        IllusionerArmour.initialise();
        TrainingWeights.initialise();
        SummonersGarb.initialise();
    }
}