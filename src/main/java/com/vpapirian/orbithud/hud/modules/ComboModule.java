package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.ComboTracker;
import com.vpapirian.orbithud.hud.TextModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

/** Consecutive hits you've landed without being hit back. */
public class ComboModule extends TextModule {
	private static final ComboTracker TRACKER = new ComboTracker(2000);
	private int lastHurtTime;

	public ComboModule() {
		super("combo", "Combo", 4, 168);
	}

	public static void onAttack(Entity entity) {
		if (entity instanceof LivingEntity) TRACKER.hit(System.currentTimeMillis());
	}

	@Override
	public void tick() {
		if (mc.player == null) return;
		int hurt = mc.player.hurtTime;
		if (hurt > 0 && lastHurtTime == 0) TRACKER.damaged();
		lastHurtTime = hurt;
	}

	@Override
	protected String text(boolean preview) {
		int combo = preview ? 5 : TRACKER.combo(System.currentTimeMillis());
		return "Combo: " + combo;
	}
}
