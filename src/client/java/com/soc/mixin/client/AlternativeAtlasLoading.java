package com.soc.mixin.client;

import com.google.gson.JsonObject;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JsonUnbakedModel.Deserializer.class)
public abstract class AlternativeAtlasLoading {
    @Inject(method = "texturesFromJson", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;getObject(Lcom/google/gson/JsonObject;Ljava/lang/String;)Lcom/google/gson/JsonObject;"), cancellable = true)
    private void socwars_alternativeAtlasLoading(JsonObject json, CallbackInfoReturnable<ModelTextures.Textures> cir) {
        if (json.has("atlas")) {
            cir.setReturnValue(ModelTextures.fromJson(JsonHelper.getObject(json, "textures"), Identifier.of(json.get("atlas").getAsString())));
        }
    }
}