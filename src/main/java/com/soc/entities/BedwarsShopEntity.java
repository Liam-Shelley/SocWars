package com.soc.entities;

import com.soc.entities.util.ModEntities;
import com.soc.game.manager.BedwarsGameManager;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.OptionalInt;

public class BedwarsShopEntity extends LivingEntity {
    static {
        FabricDefaultAttributeRegistry.register(ModEntities.BEDWARS_SHOP, BedwarsShopEntity.createBedwarsShopAttributes());
    }

    private final Identifier skinTexture;

    public BedwarsShopEntity(EntityType<? extends BedwarsShopEntity> entityType, World world) {
        super(entityType, world);
        this.skinTexture = null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static BedwarsShopEntity spawnWithPos(World world, Vec3d pos) {
        final BedwarsShopEntity entity = new BedwarsShopEntity(ModEntities.BEDWARS_SHOP, world);
        entity.setPosition(pos);
        world.spawnEntity(entity);
        return entity;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    protected void readCustomData(ReadView view) {

    }

    @Override
    protected void writeCustomData(WriteView view) {

    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (hand == Hand.OFF_HAND) return ActionResult.PASS;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            final OptionalInt syncId = player.openHandledScreen(new SimpleNamedScreenHandlerFactory(BedwarsShopScreenHandler::new, Text.of("Individual Shop")));
            BedwarsGameManager.sendShopData(serverPlayer, syncId);
        }
        return player.distanceTo(this) < 10 ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public boolean isInvulnerable() {
        return this.getPos().x > -256d;
    }

    @Override
    public void kill(ServerWorld world) {
        this.discard();
    }

    public static DefaultAttributeContainer.Builder createBedwarsShopAttributes() {
        return LivingEntity.createLivingAttributes();
    }
}

