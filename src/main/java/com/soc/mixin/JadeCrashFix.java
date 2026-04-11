package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.jade.addon.universal.ItemCollector;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

@Mixin(value = ItemCollector.class, remap = false)
abstract class JadeCrashFix {
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lsnownee/jade/addon/universal/ItemIterator;getVersion(Ljava/lang/Object;)J"), cancellable = true)
    private void socwars_jadeCrashFix(Accessor<?> accessor, CallbackInfoReturnable<List<ViewGroup<ItemStack>>> cir, @Local Object container) {
        if (container instanceof ItemEntity) cir.setReturnValue(null);
    }
}
