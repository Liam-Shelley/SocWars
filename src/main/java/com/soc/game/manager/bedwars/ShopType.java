package com.soc.game.manager.bedwars;

import com.soc.entities.BedwarsShopEntity;
import com.soc.entities.util.ModEntities;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.ScreenHandlerFactory;

public enum ShopType {
    INDIVIDUAL(BedwarsShopScreenHandler::new, ModEntities.INDIVIDUAL_BEDWARS_SHOP),
    TEAM(null, ModEntities.TEAM_BEDWARS_SHOP);

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
}
