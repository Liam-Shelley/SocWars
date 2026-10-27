package com.soc.items.util;

import com.soc.items.*;

public interface ModItems {
    static void initialise() {
        ModItemGroups.initialise();

        HardCodes.initialise();

        DrawableWeapon.initialise();
        BowItem.initialise();
        PotionRing.initialise();
        PotionFood.initialise();
        GamblerArmour.initialise();
        GamblerSword.initialise();
        SteadfastArmour.initialise();
        CartoonArmour.initialise();
        GlassArmour.initialise();
        DemolitionistArmour.initialise();
        IllusionerArmour.initialise();
        TrainingWeights.initialise();
        SummonersGarb.initialise();

        BlockItems.initialise();
        BaseTool.initialise();
        BaseItem.initialise();
        DoubleJumpItem.initialise();
        JetShoppingTrolley.initialise();
        MorphWand.initialise();
        BothClickItem.initialise();
        GravityOrb.initialise();
    }
}