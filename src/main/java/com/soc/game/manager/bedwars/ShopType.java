package com.soc.game.manager.bedwars;

import com.soc.entities.BedwarsShopEntity;
import com.soc.entities.util.ModEntities;
import com.soc.game.map.DyeColourWithEmpty;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.ScreenHandlerFactory;

public enum ShopType {
    INDIVIDUAL(BedwarsIndividualShopScreenHandler::new, ModEntities.INDIVIDUAL_BEDWARS_SHOP),
    TEAM(BedwarsTeamShopScreenHandler::new, ModEntities.TEAM_BEDWARS_SHOP);

    private final ScreenHandlerFactory factory;
    private final EntityType<BedwarsShopEntity> entityType;

    ShopType(ScreenHandlerFactory factory, EntityType<BedwarsShopEntity> entityType) {
        this.factory = factory;
        this.entityType = entityType;
    }

    public ScreenHandlerFactory getFactory() {
        return this.factory;
    }

    public EntityType<BedwarsShopEntity> getEntityType() {
        return this.entityType;
    }

    public static ShopType fromOrdinal(int ordinal) {
        final ShopType[] values = ShopType.values();
        return values[ordinal < values.length ? ordinal : 0];
    }
}
