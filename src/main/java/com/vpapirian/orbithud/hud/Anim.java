package com.vpapirian.orbithud.hud;

/** Small animation helpers. No Minecraft types, so they can be unit tested. */
public final class Anim {
	private Anim() {}

	/**
	 * Moves {@code current} toward {@code target} at {@code ratePerSecond} (1.0 = a full 0 to 1
	 * transition per second) without overshooting. Frame-rate independent.
	 */
	public static float approach(float current, float target, float seconds, float ratePerSecond) {
		float step = Math.max(0f, seconds) * ratePerSecond;
		if (current < target) return Math.min(target, current + step);
		return Math.max(target, current - step);
	}

	public static float easeOutCubic(float t) {
		float c = Math.max(0f, Math.min(1f, t));
		float inv = 1f - c;
		return 1f - inv * inv * inv;
	}

	/** Linear blend of two ARGB colors, channel by channel. */
	public static int lerpColor(int from, int to, float t) {
		float c = Math.max(0f, Math.min(1f, t));
		int a = lerp(from >>> 24, to >>> 24, c);
		int r = lerp((from >> 16) & 0xFF, (to >> 16) & 0xFF, c);
		int g = lerp((from >> 8) & 0xFF, (to >> 8) & 0xFF, c);
		int b = lerp(from & 0xFF, to & 0xFF, c);
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	private static int lerp(int a, int b, float t) {
		return Math.round(a + (b - a) * t);
	}
}
