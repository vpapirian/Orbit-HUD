package com.vpapirian.orbithud.hud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReachMathTest {
	// A 0.6 x 1.8 x 0.6 box standing on the origin, like a player.
	private static double dist(double px, double py, double pz) {
		return ReachMath.distanceToBox(px, py, pz, -0.3, 0, -0.3, 0.3, 1.8, 0.3);
	}

	@Test
	void distanceInFrontOfAFace() {
		assertEquals(2.7, dist(3.0, 1.0, 0.0), 1e-9);
	}

	@Test
	void pointInsideTheBoxIsZero() {
		assertEquals(0.0, dist(0.0, 1.0, 0.0), 1e-9);
	}

	@Test
	void distanceToACorner() {
		// 3 blocks past x and z faces horizontally, level with the box: sqrt(3^2 + 4^2) = 5
		assertEquals(5.0, ReachMath.distanceToBox(3.3, 1.0, 4.3, -0.3, 0, -0.3, 0.3, 1.8, 0.3), 1e-9);
	}

	@Test
	void distanceFromAbove() {
		assertEquals(1.2, dist(0.0, 3.0, 0.0), 1e-9);
	}
}
