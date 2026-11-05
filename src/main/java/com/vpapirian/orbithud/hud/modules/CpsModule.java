package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.CpsTracker;
import com.vpapirian.orbithud.hud.TextModule;

public class CpsModule extends TextModule {
	public CpsModule() {
		super("cps", "CPS", 4, 92);
		defineFlag("Show right click", true);
	}

	@Override
	protected String text(boolean preview) {
		return flag("Show right click")
				? "CPS: " + CpsTracker.left() + " | " + CpsTracker.right()
				: "CPS: " + CpsTracker.left();
	}
}
