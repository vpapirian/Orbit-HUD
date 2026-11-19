package com.vpapirian.orbithud.gui;

import net.minecraft.client.gui.DrawContext;

/** Rounded rectangles built from horizontal strips, so they work with plain {@code fill} calls. */
public final class Draw {
	private Draw() {}

	/**
	 * How many pixels the left and right edges are pulled in on row {@code row} (0 = outermost)
	 * of a corner with the given radius.
	 */
	public static int cornerInset(int radius, int row) {
		if (radius <= 0 || row < 0 || row >= radius) return 0;
		double dy = radius - row - 0.5;
		double dx = Math.sqrt(radius * radius - dy * dy);
		return Math.max(0, (int) Math.ceil(radius - dx - 0.25));
	}

	/** Fills the box (x1, y1) to (x2, y2) with rounded corners. Each pixel is drawn once, so alpha is safe. */
	public static void roundedRect(DrawContext ctx, int x1, int y1, int x2, int y2, int radius, int color) {
		int w = x2 - x1;
		int h = y2 - y1;
		if (w <= 0 || h <= 0) return;
		int r = Math.min(radius, Math.min(w, h) / 2);
		if (r <= 0) {
			ctx.fill(x1, y1, x2, y2, color);
			return;
		}
		for (int row = 0; row < r; row++) {
			int inset = cornerInset(r, row);
			ctx.fill(x1 + inset, y1 + row, x2 - inset, y1 + row + 1, color);
			ctx.fill(x1 + inset, y2 - row - 1, x2 - inset, y2 - row, color);
		}
		ctx.fill(x1, y1 + r, x2, y2 - r, color);
	}

	/** A rounded box with a one pixel border: {@code border} drawn first, {@code fill} inset by one. */
	public static void roundedBordered(DrawContext ctx, int x1, int y1, int x2, int y2, int radius, int border, int fill) {
		roundedRect(ctx, x1, y1, x2, y2, radius, border);
		roundedRect(ctx, x1 + 1, y1 + 1, x2 - 1, y2 - 1, Math.max(0, radius - 1), fill);
	}

	/** Soft shadow: a few faint rounded rects growing outward, drawn before the panel. */
	public static void shadow(DrawContext ctx, int x1, int y1, int x2, int y2, int radius, int spread) {
		for (int i = spread; i >= 1; i--) {
			int alpha = Math.max(1, 0x1C / i);
			roundedRect(ctx, x1 - i, y1 - i + 1, x2 + i, y2 + i + 1, radius + i, alpha << 24);
		}
	}
}
