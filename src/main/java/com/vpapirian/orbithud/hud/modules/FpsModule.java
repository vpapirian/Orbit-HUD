package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.TextModule;

public class FpsModule extends TextModule {
	public FpsModule() {
		super("fps", "FPS", 4, 111);
	}

	@Override
	protected String text(boolean preview) {
		return "FPS: " + mc.getCurrentFps();
	}
}
