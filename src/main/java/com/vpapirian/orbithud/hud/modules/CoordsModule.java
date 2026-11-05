package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.TextModule;

public class CoordsModule extends TextModule {
	public CoordsModule() {
		super("coords", "Coordinates", 4, 149);
		defineFlag("Show facing", true);
	}

	@Override
	protected String text(boolean preview) {
		var p = mc.player;
		String s = "XYZ: " + p.getBlockX() + ", " + p.getBlockY() + ", " + p.getBlockZ();
		if (flag("Show facing")) {
			String dir = p.getHorizontalFacing().asString();
			s += " (" + Character.toUpperCase(dir.charAt(0)) + dir.substring(1) + ")";
		}
		return s;
	}
}
