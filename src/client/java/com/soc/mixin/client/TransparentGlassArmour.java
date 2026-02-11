package com.soc.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.items.util.TransparentArmour;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EquipmentRenderer.class)
public abstract class TransparentGlassArmour {
    @Redirect(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer socwars_transparentGlassArmour(Identifier texture, @Local(argsOnly = true) ItemStack stack) {
        return stack.getItem() instanceof TransparentArmour ? RenderLayer.createArmorTranslucent(texture) : RenderLayer.getArmorCutoutNoCull(texture);
    }
}
