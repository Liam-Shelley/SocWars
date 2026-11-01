package com.soc.item;

import com.google.gson.JsonElement;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record OptionalProperty(Identifier component, String field, @NotNull Cache cache) implements BooleanProperty { //TODO: Nuke this whole thing into orbit - maybe it's actually not that bad now that I cache it?
	public static final MapCodec<OptionalProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("component").forGetter(OptionalProperty::component),
			Codec.STRING.fieldOf("field").forGetter(OptionalProperty::field)
	).apply(instance, OptionalProperty::new));

	public OptionalProperty(Identifier component, String field) {
		this(component, field, new Cache(null, false));
	}

	public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
		final Optional<ComponentType<?>> componentType = Registries.DATA_COMPONENT_TYPE.getOptionalValue(this.component);
		if (componentType.isEmpty()) return false;

		final Object componentValue = stack.get(componentType.get());
		if (componentValue == null) return false;

		if (this.cache.matches(componentValue)) {
			return this.cache.result;
		}

		final JsonElement element = serialise(componentValue, componentType.get().getCodec());
		if (element == null || !element.isJsonObject()) return false;

		final JsonElement fieldElement = element.getAsJsonObject().get(this.field);
		if (fieldElement == null) return false;

		final boolean result = fieldElement.toString().length() > 2;
		this.cache.update(componentValue, result);
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <T> JsonElement serialise(Object componentValue, Codec<T> codec) {
		if (codec == null) return null;

		final DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, (T)componentValue);
		if (result.isError()) return null;
		return result.getOrThrow();
	}

	public MapCodec<OptionalProperty> getCodec() {
		return CODEC;
	}

	private static class Cache {
		private Object object;
		private boolean result;

		private Cache(Object object, boolean result) {
			this.object = object;
			this.result = result;
		}

		private boolean matches(Object object) {
			return Objects.equals(object, this.object);
		}

		private void update(Object object, boolean result) {
			this.object = object;
			this.result = result;
		}
	}
}
