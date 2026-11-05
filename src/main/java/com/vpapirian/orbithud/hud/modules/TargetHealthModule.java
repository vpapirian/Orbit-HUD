package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.ColorUtil;
import com.vpapirian.orbithud.hud.HudModule;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

/** Health bar for the mob you're looking at, or the last one you hit (for 3 seconds). */
public class TargetHealthModule extends HudModule {
	private static final int W = 130;
	private static final long HOLD_MS = 3000;

	private static LivingEntity lastHit;
	private static long lastHitTime;

	public TargetHealthModule() {
		super("target", "Mob Health", CENTER, CENTER);
		width = W;
		height = 30;
		defineFlag("Health gradient", true);
		defineFlag("Show when looking", true);
	}

	public static void onAttack(Entity entity) {
		if (entity instanceof LivingEntity living) {
			lastHit = living;
			lastHitTime = System.currentTimeMillis();
		}
	}

	private LivingEntity currentTarget() {
		if (flag("Show when looking") && mc.targetedEntity instanceof LivingEntity looked) return looked;
		if (lastHit != null && lastHit.isAlive() && System.currentTimeMillis() - lastHitTime < HOLD_MS) return lastHit;
		return null;
	}

	@Override
	protected void render(DrawContext ctx, boolean preview) {
		width = W;
		height = 30;
		LivingEntity t = currentTarget();
		if (t == null && !preview) return;

		String name = t == null ? "Zombie" : t.getDisplayName().getString();
		float hp = t == null ? 14f : t.getHealth();
		float max = t == null ? 20f : Math.max(1f, t.getMaxHealth());
		float absorption = t == null ? 0f : t.getAbsorptionAmount();
		float frac = hp / max;

		TextRenderer tr = mc.textRenderer;
		ctx.fill(0, 0, width, height, backgroundColor);

		String hpText = String.format("%.1f ❤", hp + absorption);
		int hpColor = flag("Health gradient") ? ColorUtil.health(frac) : color();
		int nameMax = width - 10 - tr.getWidth(hpText) - 4;
		ctx.drawText(tr, tr.trimToWidth(name, nameMax), 5, 5, color(), true);
		ctx.drawText(tr, hpText, width - 5 - tr.getWidth(hpText), 5, hpColor, true);

		int barX = 5, barY = 18, barW = width - 10, barH = 6;
		ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF2A2A2A);
		ctx.fill(barX, barY, barX + Math.round(barW * Math.min(1f, frac)), barY + barH, hpColor);
		if (absorption > 0) {
			int absW = Math.round(barW * Math.min(1f, absorption / max));
			ctx.fill(barX, barY, barX + absW, barY + 2, 0xFFFFCC33);
		}
	}
}
