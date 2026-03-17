package com.soc.mixin.client;

import com.soc.effects.util.ModEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
        final int perplexed = getLevel(ModEffects.PERPLEXITY);
        final int arthrodesisLevel = getLevel(ModEffects.ARTHRODESIS);

        final GameOptions settings = MinecraftClient.getInstance().options;

        final boolean sneakInput = switch(arthrodesisLevel) {
            case -1 -> perplexed > 1 ? settings.sprintKey.isPressed() : settings.sneakKey.isPressed();
            case 0 -> false;
            default -> true;
        };

        super.playerInput = new PlayerInput(
                perplexed > 0 ? settings.backKey.isPressed() : settings.forwardKey.isPressed(),
                perplexed > 0 ? settings.forwardKey.isPressed() : settings.backKey.isPressed(),
                perplexed > 0 ? settings.rightKey.isPressed() : settings.leftKey.isPressed(),
                perplexed > 0 ? settings.leftKey.isPressed() : settings.rightKey.isPressed(),
                settings.jumpKey.isPressed(),
                sneakInput,
                perplexed > 1 ? settings.sneakKey.isPressed() : settings.sprintKey.isPressed()
        );
        float vertical = getMovementMultiplier(super.playerInput.forward(), super.playerInput.backward());
        float horizontal = getMovementMultiplier(super.playerInput.left(), super.playerInput.right());
        this.movementVector = new Vec2f(horizontal, vertical).normalize();

        ci.cancel();
    }

    @Unique
    private static int getLevel(RegistryEntry<StatusEffect> effect) {
        final StatusEffectInstance instance = MinecraftClient.getInstance().player.getStatusEffect(effect);
        return instance == null ? -1 : instance.getAmplifier();
    }
}