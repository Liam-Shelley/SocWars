package com.soc.entities;

import com.soc.entities.util.ModEntities;
import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.GamesManager;
import net.minecraft.entity.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlueShellEntity extends RedShellEntity implements Ownable {
    private AbstractGameManager<?, ?, ?> gameManager;

    public BlueShellEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public BlueShellEntity(World world, Vec3d pos, @Nullable Entity owner) {
        super(ModEntities.BLUE_SHELL, world);
        this.setPosition(pos);
        this.setOwner(owner);

        this.gameManager = GamesManager.getInstance().getGame(owner).orElse(null);
    }

    @Override
    protected void findTarget() {
        if(this.gameManager != null) this.target = this.gameManager.getWinningPlayer(this.getOwner());
    }
}
