package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.TextModule;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Your real-world local time. */
public class ClockModule extends TextModule {
	private static final DateTimeFormatter H24 = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter H24_SECONDS = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final DateTimeFormatter H12 = DateTimeFormatter.ofPattern("h:mm a");
	private static final DateTimeFormatter H12_SECONDS = DateTimeFormatter.ofPattern("h:mm:ss a");

	public ClockModule() {
		super("clock", "Clock", 4, 206);
		defineFlag("24-hour", true);
		defineFlag("Show seconds", false);
	}

	@Override
	protected String text(boolean preview) {
		boolean h24 = flag("24-hour");
		boolean seconds = flag("Show seconds");
		DateTimeFormatter f = h24 ? (seconds ? H24_SECONDS : H24) : (seconds ? H12_SECONDS : H12);
		return LocalTime.now().format(f);
	}
}
