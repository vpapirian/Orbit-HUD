package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.ColorUtil;
import com.vpapirian.orbithud.hud.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ArmorModule extends HudModule {
	public ArmorModule() {
		super("armor", "Armor Status", 10000, 110);
		defineFlag("Show held item", true);
		defineFlag("Color durability", true);
		defineFlag("Hide background", true);
	}

	@Override
	protected void render(DrawContext ctx, boolean preview) {
		if (mc.player == null) return;
		List<ItemStack> items = new ArrayList<>();
		for (ItemStack s : mc.player.getArmorItems()) items.add(0, s); // helmet first
		if (flag("Show held item")) items.add(mc.player.getMainHandStack());
		items.removeIf(ItemStack::isEmpty);

		if (items.isEmpty() && preview) {
			items = List.of(new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.DIAMOND_CHESTPLATE),
					new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND_BOOTS));
		}

		int textWidth = 0;
		for (ItemStack s : items) textWidth = Math.max(textWidth, mc.textRenderer.getWidth(label(s)));
		width = 22 + textWidth + 4;
		height = Math.max(16, items.size() * 18 + 2);
		drawBackground(ctx);

		int y = 1;
		for (ItemStack s : items) {
			ctx.drawItem(s, 2, y);
			int c = color();
			if (flag("Color durability") && s.isDamageable()) {
				c = ColorUtil.health(1f - (float) s.getDamage() / s.getMaxDamage());
			}
			ctx.drawText(mc.textRenderer, label(s), 22, y + 4, c, true);
			y += 18;
		}
	}

	private static String label(ItemStack s) {
		if (s.isDamageable()) return String.valueOf(s.getMaxDamage() - s.getDamage());
		return s.getCount() > 1 ? String.valueOf(s.getCount()) : "";
	}
}
