package com.soc.mixin;

import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerInteractCancel {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;getOffers()Lnet/minecraft/village/TradeOfferList;"), method = "interactMob", cancellable = true)
	void socwars_villagerInteractCancel(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		player.openHandledScreen(new SimpleNamedScreenHandlerFactory(BedwarsShopScreenHandler::new, Text.of("Shop")));
		cir.setReturnValue(ActionResult.SUCCESS);
	}
}