package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.HudModule;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class PotionModule extends HudModule {
	private static final String[] ROMAN = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

	public PotionModule() {
		super("potions", "Potion Effects", 4, 172);
		defineFlag("Show icons", true);
		defineFlag("Hide background", true);
	}

	@Override
	protected void render(DrawContext ctx, boolean preview) {
		if (mc.player == null || mc.world == null) return;
		TextRenderer tr = mc.textRenderer;
		List<StatusEffectInstance> effects = new ArrayList<>(mc.player.getStatusEffects());
		boolean icons = flag("Show icons");
		int textX = icons ? 22 : 4;

		if (effects.isEmpty()) {
			width = 90;
			height = preview ? 22 : 0;
			if (preview) {
				drawBackground(ctx);
				ctx.drawText(tr, "Speed II", textX, 3, color(), true);
				ctx.drawText(tr, "1:30", textX, 12, 0xFFAAAAAA, true);
			}
			return;
		}

		float tickRate = mc.world.getTickManager().getTickRate();
		int maxW = 0;
		for (StatusEffectInstance e : effects) maxW = Math.max(maxW, tr.getWidth(name(e)));
		width = textX + maxW + 4;
		height = effects.size() * 22;
		drawBackground(ctx);

		int y = 0;
		for (StatusEffectInstance e : effects) {
			RegistryEntry<StatusEffect> type = e.getEffectType();
			if (icons) {
				Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(type);
				ctx.drawSprite(2, y + 2, 0, 18, 18, sprite);
			}
			ctx.drawText(tr, name(e), textX, y + 3, color(), true);
			ctx.drawText(tr, StatusEffectUtil.getDurationText(e, 1.0f, tickRate), textX, y + 12, 0xFFAAAAAA, true);
			y += 22;
		}
	}

	private static String name(StatusEffectInstance e) {
		String n = e.getEffectType().value().getName().getString();
		int amp = e.getAmplifier();
		if (amp > 0) n += " " + (amp < ROMAN.length ? ROMAN[amp] : String.valueOf(amp + 1));
		return n;
	}
}
