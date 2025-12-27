package com.soc.mixin.client;

import com.soc.effects.util.ModEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputModifier extends Input {
    @Shadow
    private static float getMovementMultiplier(boolean positive, boolean negative) {
        return 0f;
    }

	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	private void socwars_cancelSneaking(CallbackInfo ci) {
        final MinecraftClient client = MinecraftClient.getInstance();
        boolean tonyaed = client.player.hasStatusEffect(ModEffects.ARTHRODESIS);
        boolean perplexed = client.player.hasStatusEffect(ModEffects.PERPLEXITY);

        final GameOptions settings = client.options;

        super.playerInput = new PlayerInput(
                perplexed ? settings.backKey.isPressed() : settings.forwardKey.isPressed(),
                perplexed ? settings.forwardKey.isPressed() : settings.backKey.isPressed(),
                perplexed ? settings.rightKey.isPressed() : settings.leftKey.isPressed(),
                perplexed ? settings.leftKey.isPressed() : settings.rightKey.isPressed(),
                settings.jumpKey.isPressed(),
                settings.sneakKey.isPressed() && !tonyaed,
                settings.sprintKey.isPressed()
        );
        float vertical = getMovementMultiplier(super.playerInput.forward(), super.playerInput.backward());
        float horizontal = getMovementMultiplier(super.playerInput.left(), super.playerInput.right());
        this.movementVector = new Vec2f(horizontal, vertical).normalize();

        ci.cancel();
    }
}