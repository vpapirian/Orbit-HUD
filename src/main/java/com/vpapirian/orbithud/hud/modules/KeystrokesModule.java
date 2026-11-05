package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.ColorUtil;
import com.vpapirian.orbithud.hud.CpsTracker;
import com.vpapirian.orbithud.hud.HudModule;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

public class KeystrokesModule extends HudModule {
	private static final int KEY = 22;
	private static final int GAP = 2;
	private static final int FULL = KEY * 3 + GAP * 2;

	public KeystrokesModule() {
		super("keystrokes", "Keystrokes", 4, 4);
		defineFlag("Show mouse", true);
		defineFlag("Show CPS", true);
		defineFlag("Show space", true);
		defineFlag("Text shadow", false);
	}

	@Override
	protected void render(DrawContext ctx, boolean preview) {
		GameOptions o = mc.options;
		int y = 0;

		key(ctx, o.forwardKey, KEY + GAP, y, KEY, KEY);
		y += KEY + GAP;
		key(ctx, o.leftKey, 0, y, KEY, KEY);
		key(ctx, o.backKey, KEY + GAP, y, KEY, KEY);
		key(ctx, o.rightKey, (KEY + GAP) * 2, y, KEY, KEY);
		y += KEY;

		if (flag("Show mouse")) {
			y += GAP;
			int leftW = (FULL - GAP) / 2;
			mouseKey(ctx, "LMB", CpsTracker.left(), o.attackKey.isPressed(), 0, y, leftW);
			mouseKey(ctx, "RMB", CpsTracker.right(), o.useKey.isPressed(), leftW + GAP, y, FULL - leftW - GAP);
			y += KEY;
		}

		if (flag("Show space")) {
			y += GAP;
			int fg = box(ctx, 0, y, FULL, 11, o.jumpKey.isPressed());
			ctx.fill(FULL / 2 - 12, y + 5, FULL / 2 + 12, y + 6, fg);
			y += 11;
		}

		width = FULL;
		height = y;
	}

	/** Fills a key box and returns the color to draw its label in. */
	private int box(DrawContext ctx, int x, int y, int w, int h, boolean pressed) {
		int bg = pressed ? ColorUtil.withAlpha(color(), 0xD0) : backgroundColor;
		ctx.fill(x, y, x + w, y + h, bg);
		return pressed ? 0xFF111111 : color();
	}

	private void key(DrawContext ctx, KeyBinding binding, int x, int y, int w, int h) {
		int fg = box(ctx, x, y, w, h, binding.isPressed());
		String label = binding.getBoundKeyLocalizedText().getString().toUpperCase();
		if (label.length() > 3) label = label.substring(0, 3);
		TextRenderer tr = mc.textRenderer;
		ctx.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + (h - 8) / 2, fg, flag("Text shadow"));
	}

	private void mouseKey(DrawContext ctx, String label, int cps, boolean pressed, int x, int y, int w) {
		int fg = box(ctx, x, y, w, KEY, pressed);
		TextRenderer tr = mc.textRenderer;
		boolean shadow = flag("Text shadow");
		if (!flag("Show CPS")) {
			ctx.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + 7, fg, shadow);
			return;
		}
		ctx.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + 3, fg, shadow);

		String cpsText = cps + " CPS";
		var m = ctx.getMatrices();
		m.push();
		m.translate(x + w / 2f, y + 13, 0);
		m.scale(0.75f, 0.75f, 1f);
		ctx.drawText(tr, cpsText, -tr.getWidth(cpsText) / 2, 0, fg, shadow);
		m.pop();
	}
}
