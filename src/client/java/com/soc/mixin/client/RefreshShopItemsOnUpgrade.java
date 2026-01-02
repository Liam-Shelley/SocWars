package com.soc.mixin.client;

import com.soc.game.manager.bedwars.UpgradeableShopItem;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = UpgradeableShopItem.class, remap = false)
public abstract class RefreshShopItemsOnUpgrade {
	@Inject(at = @At(value = "INVOKE", target = "Lcom/soc/game/manager/bedwars/UpgradeableShopItem;takeItems(Lnet/minecraft/entity/player/PlayerEntity;)V"), method = "buy")
	private void socwars_refreshShopItemsOnUpgrade(PlayerEntity player, BedwarsShopScreenHandler context, CallbackInfoReturnable<Boolean> cir) {
		if (MinecraftClient.getInstance().player.currentScreenHandler instanceof BedwarsShopScreenHandler bedwarsShopScreenHandler) {
			bedwarsShopScreenHandler.refreshItems();
		}
	}
}