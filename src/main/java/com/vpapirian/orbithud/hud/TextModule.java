package com.vpapirian.orbithud.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** A module that shows one line of text in a box (FPS, CPS, ping, coords...). */
public abstract class TextModule extends HudModule {

	protected TextModule(String id, String name, int x, int y) {
		super(id, name, x, y);
		defineFlag("Text shadow", true);
		defineFlag("Hide background", false);
	}

	protected abstract String text(boolean preview);

	@Override
	protected void render(DrawContext ctx, boolean preview) {
		if (mc.player == null) return;
		TextRenderer tr = mc.textRenderer;
		String t = text(preview);
		width = tr.getWidth(t) + 10;
		height = 17;
		drawBackground(ctx);
		ctx.drawText(tr, t, 5, 5, color(), flag("Text shadow"));
	}
}
