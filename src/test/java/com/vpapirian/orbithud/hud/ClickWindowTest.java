package com.vpapirian.orbithud.hud;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClickWindowTest {
	@Test
	void countsClicksInsideWindow() {
		ClickWindow w = new ClickWindow(1000);
		w.record(0);
		w.record(100);
		w.record(900);
		assertEquals(3, w.count(900));
	}

	@Test
	void dropsClicksOlderThanWindow() {
		ClickWindow w = new ClickWindow(1000);
		w.record(0);
		w.record(500);
		assertEquals(1, w.count(1200));
		assertEquals(0, w.count(2000));
	}

	@Test
	void clickExactlyOnCutoffIsKept() {
		ClickWindow w = new ClickWindow(1000);
		w.record(0);
		assertEquals(1, w.count(1000));
		assertEquals(0, w.count(1001));
	}

	@Test
	void recordingPrunesWithoutAnyReads() throws Exception {
		ClickWindow w = new ClickWindow(1000);
		for (int i = 0; i < 100_000; i++) w.record(i); // one click per ms, never read
		assertEquals(1001, size(w));
	}

	@Test
	void countAfterLongIdleIsZero() {
		ClickWindow w = new ClickWindow(1000);
		for (int i = 0; i < 20; i++) w.record(i * 10L);
		assertEquals(0, w.count(1_000_000));
	}

	@SuppressWarnings("unchecked")
	private static int size(ClickWindow w) throws Exception {
		Field f = ClickWindow.class.getDeclaredField("clicks");
		f.setAccessible(true);
		return ((Deque<Long>) f.get(w)).size();
	}
}
