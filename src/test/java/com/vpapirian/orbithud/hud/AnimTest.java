package com.vpapirian.orbithud.hud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnimTest {
	@Test
	void approachMovesTowardTheTargetAtTheGivenRate() {
		assertEquals(0.25f, Anim.approach(0f, 1f, 0.25f, 1f), 1e-6);
		assertEquals(0.75f, Anim.approach(1f, 0f, 0.25f, 1f), 1e-6);
	}

	@Test
	void approachNeverOvershoots() {
		assertEquals(1f, Anim.approach(0.9f, 1f, 1f, 10f));
		assertEquals(0f, Anim.approach(0.1f, 0f, 1f, 10f));
	}

	@Test
	void approachIgnoresNegativeTime() {
		assertEquals(0.5f, Anim.approach(0.5f, 1f, -1f, 10f));
	}

	@Test
	void easeOutCubicHasFixedEndsAndIsClamped() {
		assertEquals(0f, Anim.easeOutCubic(-1f));
		assertEquals(1f, Anim.easeOutCubic(2f));
		assertEquals(0.875f, Anim.easeOutCubic(0.5f), 1e-6);
	}

	@Test
	void lerpColorBlendsEveryChannel() {
		assertEquals(0xFF000000, Anim.lerpColor(0xFF000000, 0xFFFFFFFF, 0f));
		assertEquals(0xFFFFFFFF, Anim.lerpColor(0xFF000000, 0xFFFFFFFF, 1f));
		assertEquals(0x80808080, Anim.lerpColor(0x00000000, 0xFFFFFFFF, 0.5f));
		assertEquals(0xFF102030, Anim.lerpColor(0xFF102030, 0xFFFFFFFF, -3f));
	}
}
