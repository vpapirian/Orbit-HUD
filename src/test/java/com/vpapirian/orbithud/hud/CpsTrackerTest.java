package com.vpapirian.orbithud.hud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CpsTrackerTest {
	@BeforeEach
	void clear() {
		CpsTracker.reset();
	}

	@Test
	void countsClicksInsideTheLastSecond() {
		CpsTracker.click(0, 10_000);
		CpsTracker.click(0, 10_300);
		CpsTracker.click(1, 10_400);
		assertEquals(2, CpsTracker.left(10_500));
		assertEquals(1, CpsTracker.right(10_500));
	}

	@Test
	void dropsClicksOlderThanOneSecond() {
		CpsTracker.click(0, 10_000);
		CpsTracker.click(0, 10_900);
		// 10_000 is 1_100 ms old here, 10_900 is 200 ms old.
		assertEquals(1, CpsTracker.left(11_100));
	}

	@Test
	void clickExactlyOneSecondOldIsStillCounted() {
		CpsTracker.click(0, 10_000);
		assertEquals(1, CpsTracker.left(11_000));
		assertEquals(0, CpsTracker.left(11_001));
	}

	@Test
	void ignoresOtherMouseButtons() {
		CpsTracker.click(2, 10_000);
		assertEquals(0, CpsTracker.left(10_000));
		assertEquals(0, CpsTracker.right(10_000));
	}

	@Test
	void storedClicksStayBoundedWhenNothingReadsTheCount() {
		// Simulates a long session with every CPS-reading module disabled:
		// clicks are recorded but left()/right() are never called.
		for (long t = 0; t < 600_000; t += 100) {
			CpsTracker.click(0, t);
			CpsTracker.click(1, t);
		}
		// At 10 clicks/s only the last second's worth (plus the boundary click) may remain.
		assertEquals(11, CpsTracker.stored(0));
		assertEquals(11, CpsTracker.stored(1));
	}
}
