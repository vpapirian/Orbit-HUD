package com.vpapirian.orbithud.hud;

import net.minecraft.util.math.MathHelper;

/** Helpers for ARGB ints. */
public final class ColorUtil {
	private ColorUtil() {}

	/** Rainbow color that cycles over time, keeping the alpha of {@code base}. */
	public static int chroma(int base) {
		float hue = (System.currentTimeMillis() % 3000L) / 3000f;
		int rgb = MathHelper.hsvToRgb(hue, 0.7f, 1f);
		return (base & 0xFF000000) | (rgb & 0x00FFFFFF);
	}

	/** Red (0 hp) to green (full hp). */
	public static int health(float fraction) {
		float f = MathHelper.clamp(fraction, 0f, 1f);
		return 0xFF000000 | (MathHelper.hsvToRgb(f / 3f, 0.85f, 0.95f) & 0x00FFFFFF);
	}

	/** Channel shifts: alpha 24, red 16, green 8, blue 0. */
	public static int channel(int color, int shift) {
		return (color >>> shift) & 0xFF;
	}

	public static int withChannel(int color, int shift, int value) {
		int v = MathHelper.clamp(value, 0, 255);
		return (color & ~(0xFF << shift)) | (v << shift);
	}

	public static int withAlpha(int color, int alpha) {
		return withChannel(color, 24, alpha);
	}
}
