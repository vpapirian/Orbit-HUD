package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.TextModule;

/** Memory the game is using, out of what the JVM may allocate. */
public class MemoryModule extends TextModule {
	private static final long MB = 1024L * 1024L;

	public MemoryModule() {
		super("memory", "Memory", 4, 225);
	}

	@Override
	protected String text(boolean preview) {
		Runtime rt = Runtime.getRuntime();
		long used = (rt.totalMemory() - rt.freeMemory()) / MB;
		long max = rt.maxMemory() / MB;
		return "Memory: " + used + " / " + max + " MB";
	}
}
