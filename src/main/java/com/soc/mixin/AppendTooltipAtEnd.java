package com.soc.mixin;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class AppendTooltipAtEnd {
    @Shadow public abstract Item getItem();

    @Redirect(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V"))
    private void socwars_cancelFirstAppendTooltip(Item instance, ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {}

    @Inject(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/tooltip/TooltipType;isAdvanced()Z"))
    @SuppressWarnings("deprecation")
    private void socwars_reappendTooltipLast(Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        this.getItem().appendTooltip((ItemStack)(Object)this, context, displayComponent, textConsumer, type);
    }
}
