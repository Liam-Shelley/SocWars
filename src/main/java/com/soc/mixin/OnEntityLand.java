package com.soc.mixin;

import com.soc.items.components.ModComponents;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class OnEntityLand{
    @Inject(method = "onEntityLand", at = @At("HEAD"))
    private void socwars_onEntityLand(BlockView world, Entity entity, CallbackInfo ci) {
        if (entity.getType() == EntityType.PLAYER) {
            PlayerEntity player = (PlayerEntity)entity;
            player.getInventory().forEach(stack -> {
                if (stack.get(ModComponents.DOUBLE_JUMP) != null) {
                    stack.set(ModComponents.DOUBLE_JUMP, true);
                }
            });
        }
    }
}
