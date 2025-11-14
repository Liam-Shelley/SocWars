package com.soc.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BedwarsShopMain extends Screen {
    private final Screen parent;

    protected BedwarsShopMain(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
