package com.vpapirian.orbithud.hud;

/** Distance helpers that don't depend on Minecraft types, so they can be unit tested. */
public final class ReachMath {
	private ReachMath() {}

	/** Distance from a point to the nearest point of an axis-aligned box (0 if the point is inside). */
	public static double distanceToBox(double px, double py, double pz,
									   double minX, double minY, double minZ,
									   double maxX, double maxY, double maxZ) {
		double dx = clamp(px, minX, maxX) - px;
		double dy = clamp(py, minY, maxY) - py;
		double dz = clamp(pz, minZ, maxZ) - pz;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	private static double clamp(double v, double lo, double hi) {
		return Math.max(lo, Math.min(hi, v));
	}
}
