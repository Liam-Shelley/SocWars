package com.soc.player;

import com.soc.networking.c2s.RequestOpenQueueScreenPayload;
import com.soc.resourcedata.deserialisation.SkywarsItemData;
import com.soc.resourcedata.listeners.SkywarsLootData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KeyBindings {
	public static final String CATEGORY = "category.socwars";
	public static final String DEBUG_CATEGORY = "category.socwars.debug";

	public static final Map<KeyBinding, Consumer<MinecraftClient>> KEY_BINDINGS = new HashMap<>();

	public static KeyBinding registerBinding(String name, int key, String category, Consumer<MinecraftClient> callback) {
		final KeyBinding binding = new KeyBinding("key.socwars." + name, InputUtil.Type.KEYSYM, key, category);

		KEY_BINDINGS.put(KeyBindingHelper.registerKeyBinding(binding), callback);

		return binding;
	}

	public static final KeyBinding PRINT_HELD_COMPONENTS = registerBinding(
			"print_held_components",
			GLFW.GLFW_KEY_H,
			DEBUG_CATEGORY,
			client -> {
				final ClientPlayerEntity player = MinecraftClient.getInstance().player;
				player.getStackInHand(Hand.MAIN_HAND).getComponents().forEach(component -> player.sendMessage(Text.literal(component.toString()), false));

				final Map<Integer, SkywarsItemData> dataMap = SkywarsLootData.INSTANCE.getSkywarsItemData().getPoolsForKey(player.getStackInHand(Hand.MAIN_HAND).getItem());
				dataMap.forEach((pool, data) -> player.sendMessage(Text.translatable("debug.skywars_item_weights", pool, data.weightT1(), data.weightT2(), data.weightT3(), data.weightT4()), false));
			}
	);
	public static final KeyBinding OPEN_QUEUE_SCREEN = registerBinding(
			"open_queue_screen",
			GLFW.GLFW_KEY_U,
			CATEGORY,
			client -> ClientPlayNetworking.send(new RequestOpenQueueScreenPayload())
	);
	public static final KeyBinding HOLD_SNEAK = registerBinding(
			"hold_sneak",
			GLFW.GLFW_KEY_RIGHT_BRACKET,
			CATEGORY,
			client -> MinecraftClient.getInstance().options.sneakKey.setPressed(!MinecraftClient.getInstance().options.sneakKey.isPressed())
	);

	public static void initialise() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> KEY_BINDINGS.forEach((keyBinding, callback) -> {
			while (keyBinding.wasPressed()) callback.accept(client);
		}));
	}
}
