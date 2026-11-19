package com.vpapirian.orbithud.hud;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Sliding window of click timestamps. Old entries are dropped whenever a click is
 * recorded, so memory use stays bounded even if nothing ever reads the count.
 * Pure logic with an explicit clock, so it can be unit tested.
 */
public final class ClickWindow {
	private final long windowMs;
	private final Deque<Long> clicks = new ArrayDeque<>();

	public ClickWindow(long windowMs) {
		this.windowMs = windowMs;
	}

	public void record(long now) {
		prune(now);
		clicks.addLast(now);
	}

	public int count(long now) {
		prune(now);
		return clicks.size();
	}

	private void prune(long now) {
		long cutoff = now - windowMs;
		while (!clicks.isEmpty() && clicks.peekFirst() < cutoff) clicks.pollFirst();
	}
}
