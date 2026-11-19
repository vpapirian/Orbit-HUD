package com.vpapirian.orbithud.hud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComboTrackerTest {
	@Test
	void consecutiveHitsBuildACombo() {
		ComboTracker t = new ComboTracker(2000);
		t.hit(1000);
		t.hit(1500);
		t.hit(2200);
		assertEquals(3, t.combo(2300));
	}

	@Test
	void takingDamageEndsTheCombo() {
		ComboTracker t = new ComboTracker(2000);
		t.hit(1000);
		t.hit(1500);
		t.damaged();
		assertEquals(0, t.combo(1600));
		t.hit(1700);
		assertEquals(1, t.combo(1700));
	}

	@Test
	void comboExpiresAfterTheTimeout() {
		ComboTracker t = new ComboTracker(2000);
		t.hit(1000);
		assertEquals(1, t.combo(3000));
		assertEquals(0, t.combo(3001));
	}

	@Test
	void hitAfterTheTimeoutStartsANewCombo() {
		ComboTracker t = new ComboTracker(2000);
		t.hit(1000);
		t.hit(1500);
		t.hit(4000);
		assertEquals(1, t.combo(4000));
	}
}
