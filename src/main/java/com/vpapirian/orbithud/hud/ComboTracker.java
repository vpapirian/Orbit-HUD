package com.vpapirian.orbithud.hud;

/**
 * Counts consecutive hits you land. The streak ends when you take damage or when
 * you go {@code timeoutMs} without landing another hit.
 */
public final class ComboTracker {
	private final long timeoutMs;
	private int combo;
	private long lastHit;

	public ComboTracker(long timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	public void hit(long now) {
		if (combo > 0 && now - lastHit > timeoutMs) combo = 0;
		combo++;
		lastHit = now;
	}

	public void damaged() {
		combo = 0;
	}

	public int combo(long now) {
		if (combo > 0 && now - lastHit > timeoutMs) combo = 0;
		return combo;
	}
}
