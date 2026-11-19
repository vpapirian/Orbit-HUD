package com.vpapirian.orbithud.hud;

/** Counts clicks in the last second. Fed by MouseMixin. */
public final class CpsTracker {
	private static final long WINDOW_MS = 1000L;
	private static final ClickWindow LEFT = new ClickWindow(WINDOW_MS);
	private static final ClickWindow RIGHT = new ClickWindow(WINDOW_MS);

	private CpsTracker() {}

	public static void click(int button) {
		long now = System.currentTimeMillis();
		if (button == 0) LEFT.record(now);
		else if (button == 1) RIGHT.record(now);
	}

	public static int left() {
		return LEFT.count(System.currentTimeMillis());
	}

	public static int right() {
		return RIGHT.count(System.currentTimeMillis());
	}
}
