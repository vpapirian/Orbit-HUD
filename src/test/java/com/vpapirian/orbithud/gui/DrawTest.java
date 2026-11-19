package com.vpapirian.orbithud.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DrawTest {
	@Test
	void noRadiusMeansNoInset() {
		for (int row = 0; row < 4; row++) assertEquals(0, Draw.cornerInset(0, row));
	}

	@Test
	void smallRadiiGiveSmoothCorners() {
		assertEquals(1, Draw.cornerInset(2, 0));
		assertEquals(0, Draw.cornerInset(2, 1));
		assertEquals(2, Draw.cornerInset(3, 0));
		assertEquals(1, Draw.cornerInset(3, 1));
		assertEquals(0, Draw.cornerInset(3, 2));
	}

	@Test
	void insetShrinksTowardTheMiddleOfTheEdge() {
		for (int r = 2; r <= 8; r++) {
			for (int row = 1; row < r; row++) {
				assertTrue(Draw.cornerInset(r, row) <= Draw.cornerInset(r, row - 1), "radius " + r + " row " + row);
			}
			assertEquals(0, Draw.cornerInset(r, r - 1) , "last row of radius " + r + " should reach the edge");
		}
	}

	@Test
	void rowsOutsideTheCornerHaveNoInset() {
		assertEquals(0, Draw.cornerInset(3, 3));
		assertEquals(0, Draw.cornerInset(3, -1));
	}
}
