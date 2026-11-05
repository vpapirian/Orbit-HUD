package com.vpapirian.orbithud.hud;

import com.vpapirian.orbithud.hud.modules.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
	public static final List<HudModule> MODULES = new ArrayList<>();

	private ModuleManager() {}

	public static void init() {
		MODULES.clear();
		// Large x values clamp to the right edge of the screen on first render.
		MODULES.add(new KeystrokesModule());
		MODULES.add(new CpsModule());
		MODULES.add(new FpsModule());
		MODULES.add(new PingModule());
		MODULES.add(new CoordsModule());
		MODULES.add(new MinimapModule());
		MODULES.add(new ArmorModule());
		MODULES.add(new PotionModule());
		MODULES.add(new TargetHealthModule());
		MODULES.add(new BossBarModule());
	}

	@SuppressWarnings("unchecked")
	public static <T extends HudModule> T get(Class<T> type) {
		for (HudModule m : MODULES) if (type.isInstance(m)) return (T) m;
		return null;
	}

	public static void tick() {
		if (MinecraftClient.getInstance().world == null) return;
		for (HudModule m : MODULES) if (m.enabled) m.tick();
	}

	public static void render(DrawContext ctx, boolean preview) {
		int sw = ctx.getScaledWindowWidth();
		int sh = ctx.getScaledWindowHeight();
		for (HudModule m : MODULES) {
			if (!m.enabled) continue;
			int w = m.getScaledWidth();
			int h = m.getScaledHeight();

			int dx = m.x == HudModule.CENTER ? (sw - w) / 2 : m.x;
			int dy = m.y == HudModule.CENTER ? sh / 2 + 24 : m.y;
			dx = MathHelper.clamp(dx, 0, Math.max(0, sw - w));
			dy = MathHelper.clamp(dy, 0, Math.max(0, sh - h));
			if (m.x != HudModule.CENTER) m.x = dx;
			if (m.y != HudModule.CENTER) m.y = dy;

			m.draw(ctx, preview, dx, dy);
		}
	}
}
