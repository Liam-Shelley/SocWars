package com.soc.mixin;

import com.soc.game.manager.BedwarsGameManager;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(VillagerEntity.class)
public abstract class BedwarsShopOpen {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;getOffers()Lnet/minecraft/village/TradeOfferList;"), method = "interactMob", cancellable = true)
	void socwars_bedwarsShopOpen(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			OptionalInt syncId = player.openHandledScreen(new SimpleNamedScreenHandlerFactory(BedwarsIndividualShopScreenHandler::new, Text.of("Shop")));
			BedwarsGameManager.sendShopData(serverPlayer, syncId);
		}
		cir.setReturnValue(ActionResult.SUCCESS);
	}
}