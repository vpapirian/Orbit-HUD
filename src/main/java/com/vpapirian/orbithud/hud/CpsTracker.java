package com.vpapirian.orbithud.hud;

import java.util.ArrayDeque;
import java.util.Deque;

/** Counts clicks in the last second. Fed by MouseMixin. */
public final class CpsTracker {
	private static final long WINDOW_MS = 1000L;
	private static final Deque<Long> LEFT = new ArrayDeque<>();
	private static final Deque<Long> RIGHT = new ArrayDeque<>();

	private CpsTracker() {}

	public static void click(int button) {
		click(button, System.currentTimeMillis());
	}

	static void click(int button, long now) {
		Deque<Long> clicks = queueFor(button);
		if (clicks == null) return;
		clicks.addLast(now);
		// Prune here as well as when reading, so the queue stays bounded even if
		// no module that shows CPS is enabled and nothing ever reads it.
		prune(clicks, now);
	}

	public static int left() {
		return count(LEFT, System.currentTimeMillis());
	}

	public static int right() {
		return count(RIGHT, System.currentTimeMillis());
	}

	static int left(long now) {
		return count(LEFT, now);
	}

	static int right(long now) {
		return count(RIGHT, now);
	}

	/** Number of clicks currently held in memory, for tests. */
	static int stored(int button) {
		Deque<Long> clicks = queueFor(button);
		return clicks == null ? 0 : clicks.size();
	}

	static void reset() {
		LEFT.clear();
		RIGHT.clear();
	}

	private static Deque<Long> queueFor(int button) {
		if (button == 0) return LEFT;
		if (button == 1) return RIGHT;
		return null;
	}

	private static int count(Deque<Long> clicks, long now) {
		prune(clicks, now);
		return clicks.size();
	}

	private static void prune(Deque<Long> clicks, long now) {
		long cutoff = now - WINDOW_MS;
		while (!clicks.isEmpty() && clicks.peekFirst() < cutoff) clicks.pollFirst();
	}
}
