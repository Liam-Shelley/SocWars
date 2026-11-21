package com.soc.gui.screen;

import com.soc.networking.c2s.DeathNoteKillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;

public class DeathNoteScreen extends Screen {
	public static final int TEXT_COLOUR = 0xff000000;
	private static final int LINES = 14;

	private final List<String> names;
	private final int maxPages;

	private int page;
	private PageTurnWidget nextPageButton;
	private PageTurnWidget previousPageButton;

	private int textX;
	private int guiX;

	private EditBoxWidget editBox;
	private String text;

	public DeathNoteScreen(List<String> names) {
		super(Text.translatable("screen.death_note"));
		this.names = names;
		this.maxPages = (int)Math.ceil((this.names.size() + 1) / (double)LINES) - 1;
	}

	@Override
	protected void init() {
		this.guiX = (this.width - 192) / 2;
		this.textX = (this.width - 114) / 2;

		this.previousPageButton = this.addDrawableChild(new PageTurnWidget(this.guiX + 43, 159, false, button -> this.updatePage(--this.page), true));
		this.nextPageButton = this.addDrawableChild(new PageTurnWidget(this.guiX + 116, 159, true, button -> this.updatePage(++this.page), true));


		this.editBox = EditBoxWidget.builder()
				.hasOverlay(false)
				.textColor(TEXT_COLOUR)
				.cursorColor(-16777216)
				.hasBackground(false)
				.textShadow(false)
				.x(this.textX)
				.y(22 + (this.names.size() % LINES) * 9)
				.build(this.textRenderer, 122, 134, ScreenTexts.EMPTY);
		this.editBox.setMaxLength(16);
		this.editBox.setMaxLines(1);
		this.editBox.setChangeListener(this::onEdit);
		this.addDrawableChild(this.editBox);

		this.page = this.maxPages;
		this.updatePage(this.maxPages);
	}

	private void updatePage(int page) {
		this.previousPageButton.visible = page > 0;
		this.nextPageButton.visible = page < this.maxPages;

		final boolean isOnMaxPage = page == this.maxPages;
		this.editBox.visible = isOnMaxPage;
		if (isOnMaxPage) {
			this.setFocused(this.editBox);
		}
	}

	private void onEdit(String text) {
		this.text = text;

		final boolean nameIsValid = MinecraftClient.getInstance().world.getPlayers().stream().anyMatch(this::playerIsValid);

		this.editBox.textColor = nameIsValid ? TEXT_COLOUR : 0xffd04040;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return switch (keyCode) {
			case GLFW.GLFW_KEY_ENTER -> this.tryKill();
			default -> super.keyPressed(keyCode, scanCode, modifiers);
		};
	}

	private boolean tryKill() {
		return Objects.requireNonNull(MinecraftClient.getInstance().world).getPlayers()
				.stream()
				.filter(this::playerIsValid)
				.findFirst()
				.map(player -> {
					ClientPlayNetworking.send(new DeathNoteKillPayload(player.getUuid()));
					MinecraftClient.getInstance().setScreen(null);
					return true;
				})
				.orElse(false);
	}

	private boolean playerIsValid(AbstractClientPlayerEntity player) {
		return player.getNameForScoreboard().equals(this.text);
	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.editBox);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);

		final int end = (this.page + 1) * LINES	;
		for (int i = this.page * LINES; i < end && i < this.names.size(); i++) {
			context.drawText(this.textRenderer, this.names.get(i), this.textX + 4, 26 + (i % LINES) * 9, TEXT_COLOUR, false);
		}

		context.drawText(this.textRenderer, String.valueOf(this.page), this.width / 2 - 10, 160, Colors.BLACK, false);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		this.renderInGameBackground(context);
		context.drawTexture(RenderPipelines.GUI_TEXTURED, BookScreen.BOOK_TEXTURE, this.guiX, 2, 0.0F, 0.0F, 192, 192, 256, 256);
	}
}
