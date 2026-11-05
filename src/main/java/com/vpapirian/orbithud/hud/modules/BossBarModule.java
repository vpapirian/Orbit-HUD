package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.ColorUtil;
import com.vpapirian.orbithud.hud.HudModule;
import com.vpapirian.orbithud.mixin.BossBarHudAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Formatting;

import java.util.Map;
import java.util.UUID;

/** Restyled, movable boss bars. Can hide the vanilla ones (see BossBarHudMixin). */
public class BossBarModule extends HudModule {
	private static final int W = 182;

	public BossBarModule() {
		super("bossbar", "Boss Health", CENTER, 2);
		width = W;
		height = 19;
		defineFlag("Hide vanilla bars", true);
		defineFlag("Use boss colors", true);
	}

	@Override
	protected void render(DrawContext ctx, boolean preview) {
		width = W;
		Map<UUID, ClientBossBar> bars = ((BossBarHudAccessor) mc.inGameHud.getBossBarHud()).orbithud$getBossBars();

		if (bars.isEmpty()) {
			height = preview ? 19 : 0;
			if (preview) drawBar(ctx, 0, "Ender Dragon", 0.65f, BossBar.Color.PINK);
			return;
		}

		int y = 0;
		for (ClientBossBar bar : bars.values()) {
			drawBar(ctx, y, bar.getName().getString(), bar.getPercent(), bar.getColor());
			y += 19;
		}
		height = y;
	}

	private void drawBar(DrawContext ctx, int y, String name, float percent, BossBar.Color bossColor) {
		TextRenderer tr = mc.textRenderer;
		ctx.drawText(tr, name, (W - tr.getWidth(name)) / 2, y, color(), true);

		int fill = color();
		if (flag("Use boss colors")) {
			Formatting f = bossColor.getTextFormat();
			Integer rgb = f.getColorValue();
			if (rgb != null) fill = 0xFF000000 | rgb;
		}
		int barY = y + 10;
		ctx.fill(0, barY, W, barY + 5, ColorUtil.withAlpha(backgroundColor, Math.max(0x90, ColorUtil.channel(backgroundColor, 24))));
		ctx.fill(0, barY, Math.round(W * percent), barY + 5, fill);
	}
}
