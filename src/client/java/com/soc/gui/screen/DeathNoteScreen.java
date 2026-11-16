package com.soc.gui.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.Optional;

public class DeathNoteScreen extends Screen {
	private EditBoxWidget editBox;
	private String text;

	protected DeathNoteScreen() {
		super(Text.translatable("screen.death_note"));
	}

	@Override
	protected void init() {
		this.editBox = EditBoxWidget.builder()
				.hasOverlay(false)
				.textColor(-16777216)
				.cursorColor(-16777216)
				.hasBackground(false)
				.textShadow(false)
				.x((this.width - 114) / 2 - 8)
				.y(28)
				.build(this.textRenderer, 122, 134, ScreenTexts.EMPTY);
		this.editBox.setMaxLength(1024);
		this.editBox.setMaxLines(126 / 9);
		this.editBox.setChangeListener(text -> this.text = text);
		this.addDrawableChild(this.editBox);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return switch (keyCode) {
			case GLFW.GLFW_KEY_ENTER -> {
				this.tryKill();
				yield true;
			}
			default -> super.keyPressed(keyCode, scanCode, modifiers);
		};
	}

	private void tryKill() {
		final Optional<PlayerEntity> targetedPlayer = Objects.requireNonNull(MinecraftClient.getInstance().world).getPlayers()
				.stream()
				.filter(player -> player.getNameForScoreboard().equals(this.text))
				.findFirst();

		ClientPlayNetworking.send()
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.editBox);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		this.renderInGameBackground(context);
		context.drawTexture(RenderPipelines.GUI_TEXTURED, BookScreen.BOOK_TEXTURE, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
	}
}
