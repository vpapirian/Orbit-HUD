package com.vpapirian.orbithud.gui;

import com.vpapirian.orbithud.OrbitHud;
import com.vpapirian.orbithud.config.HudConfig;
import com.vpapirian.orbithud.hud.ColorUtil;
import com.vpapirian.orbithud.hud.HudModule;
import com.vpapirian.orbithud.hud.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * The Right Shift menu: a grid of module cards on the left (click the strip to toggle,
 * click the card to select) and a settings panel on the right with color sliders.
 */
public class ModMenuScreen extends Screen {
	private static final Object THEME = new Object();

	private static final int PANEL_BG = 0xF0121218;
	private static final int CARD_BG = 0xFF1D1D25;
	private static final int CARD_HOVER = 0xFF262630;
	private static final int OFF = 0xFF35353F;
	private static final int MUTED = 0xFF8A8A9A;
	private static final int SETTINGS_W = 150;
	private static final int CARD_H = 44;
	private static final int GAP = 6;

	private int px, py, pw, ph;
	private int lx, ly, lw, lh;
	private int sx, sy, sh;
	private int scroll;

	private Object selected;
	private int colorTab; // 0 = text, 1 = background
	private final List<Control> controls = new ArrayList<>();
	private Control dragging;

	public ModMenuScreen() {
		super(Text.literal("Orbit HUD"));
	}

	@Override
	protected void init() {
		pw = Math.min(420, width - 10);
		ph = Math.min(260, height - 10);
		px = (width - pw) / 2;
		py = (height - ph) / 2;

		lx = px + 8;
		ly = py + 26;
		lw = pw - 16 - SETTINGS_W - 8;
		lh = ph - 26 - 34;

		sx = lx + lw + 8;
		sy = py + 26;
		sh = ph - 34;

		if (selected == null) selected = ModuleManager.MODULES.get(0);
		rebuild();
	}

	// ---------------------------------------------------------------- layout

	private List<Object> entries() {
		List<Object> list = new ArrayList<>();
		list.add(THEME);
		list.addAll(ModuleManager.MODULES);
		return list;
	}

	private int columns() {
		return Math.max(1, (lw + GAP) / (84 + GAP));
	}

	private int cardWidth() {
		int cols = columns();
		return (lw - (cols - 1) * GAP) / cols;
	}

	private int contentHeight() {
		int rows = (entries().size() + columns() - 1) / columns();
		return rows * (CARD_H + GAP) - GAP;
	}

	private void rebuild() {
		controls.clear();
		int x = sx + 8;
		int w = SETTINGS_W - 16;
		int y = sy + 22;

		if (selected instanceof HudModule m) {
			controls.add(new Toggle(x, y, w, "Enabled", () -> m.enabled, () -> m.enabled = !m.enabled));
			y += 16;

			int half = (w - 4) / 2;
			controls.add(new Button(x, y, half, 13, "Text", () -> colorTab == 0, () -> { colorTab = 0; rebuild(); }));
			controls.add(new Button(x + half + 4, y, w - half - 4, 13, "Background", () -> colorTab == 1, () -> { colorTab = 1; rebuild(); }));
			y += 18;

			IntSupplier get = colorTab == 0 ? () -> m.textColor : () -> m.backgroundColor;
			IntConsumer set = colorTab == 0 ? c -> m.textColor = c : c -> m.backgroundColor = c;
			y = addColorSliders(x, y, w, get, set);

			if (colorTab == 0) {
				controls.add(new Toggle(x, y, w, "Chroma", () -> m.chroma, () -> m.chroma = !m.chroma));
				y += 15;
			}
			controls.add(new Slider(x, y, w, "Scale", 0.5, 2.0, () -> m.scale,
					v -> m.scale = Math.round(v * 20) / 20f, v -> String.format("%.2fx", v)));
			y += 19;

			for (String key : m.flags.keySet()) {
				if (y + 12 > sy + sh - 4) break;
				controls.add(new Toggle(x, y, w, key, () -> m.flag(key), () -> m.flags.put(key, !m.flag(key))));
				y += 15;
			}
		} else {
			y += 2;
			y = addColorSliders(x, y, w, () -> OrbitHud.accent, c -> OrbitHud.accent = ColorUtil.withAlpha(c, 0xFF));
			y += 4;
			controls.add(new Button(x, y, w, 14, "Apply as text color", () -> false, () -> {
				for (HudModule mod : ModuleManager.MODULES) mod.textColor = OrbitHud.accent;
			}));
			y += 18;
			controls.add(new Button(x, y, w, 14, "Chroma: all on", () -> false, () -> {
				for (HudModule mod : ModuleManager.MODULES) mod.chroma = true;
			}));
			y += 18;
			controls.add(new Button(x, y, w, 14, "Chroma: all off", () -> false, () -> {
				for (HudModule mod : ModuleManager.MODULES) mod.chroma = false;
			}));
		}

		// Bottom bar
		int by = py + ph - 26;
		int bw = (lw - 2 * GAP) / 3;
		controls.add(new Button(lx, by, bw, 18, "Edit Layout", () -> false,
				() -> client.setScreen(new HudEditorScreen(this))));
		controls.add(new Button(lx + bw + GAP, by, bw, 18, "Reset All", () -> false, this::resetLayout));
		controls.add(new Button(lx + 2 * (bw + GAP), by, lw - 2 * (bw + GAP), 18, "Done", () -> true, this::close));
	}

	private int addColorSliders(int x, int y, int w, IntSupplier get, IntConsumer set) {
		String[] labels = {"Red", "Green", "Blue", "Opacity"};
		int[] shifts = {16, 8, 0, 24};
		for (int i = 0; i < 4; i++) {
			int shift = shifts[i];
			controls.add(new Slider(x, y, w, labels[i], 0, 255,
					() -> ColorUtil.channel(get.getAsInt(), shift),
					v -> set.accept(ColorUtil.withChannel(get.getAsInt(), shift, (int) Math.round(v))),
					v -> String.valueOf((int) Math.round(v))).tint(shift));
			y += 19;
		}
		return y;
	}

	private void resetLayout() {
		ModuleManager.init();
		selected = ModuleManager.MODULES.get(0);
		rebuild();
	}

	// ---------------------------------------------------------------- rendering

	@Override
	public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
		// No blur: keep the HUD visible behind the menu so color changes show live.
		ctx.fill(0, 0, width, height, 0x70000000);
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		int accent = OrbitHud.accent;

		ctx.fill(px, py, px + pw, py + ph, PANEL_BG);
		ctx.fill(px, py, px + pw, py + 2, accent);
		ctx.drawText(textRenderer, "ORBIT HUD", px + 9, py + 10, accent, false);
		String hint = "Right Shift to close";
		ctx.drawText(textRenderer, hint, px + pw - 9 - textRenderer.getWidth(hint), py + 10, MUTED, false);

		renderCards(ctx, mouseX, mouseY, accent);

		// Settings panel
		ctx.fill(sx, sy, sx + SETTINGS_W, sy + sh, 0xFF18181F);
		String title = selected instanceof HudModule m ? m.name : "Theme";
		ctx.drawText(textRenderer, title, sx + 8, sy + 7, 0xFFFFFFFF, false);
		int swatch = currentSwatch();
		ctx.fill(sx + SETTINGS_W - 20, sy + 5, sx + SETTINGS_W - 8, sy + 15, 0xFF000000);
		ctx.fill(sx + SETTINGS_W - 19, sy + 6, sx + SETTINGS_W - 9, sy + 14, swatch);

		for (Control c : controls) c.render(ctx, mouseX, mouseY);
	}

	private int currentSwatch() {
		if (selected instanceof HudModule m) return colorTab == 0 ? m.color() : m.backgroundColor;
		return OrbitHud.accent;
	}

	private void renderCards(DrawContext ctx, int mouseX, int mouseY, int accent) {
		ctx.enableScissor(lx, ly, lx + lw, ly + lh);
		List<Object> list = entries();
		int cols = columns();
		int cw = cardWidth();
		for (int i = 0; i < list.size(); i++) {
			Object e = list.get(i);
			int cx = lx + (i % cols) * (cw + GAP);
			int cy = ly + (i / cols) * (CARD_H + GAP) - scroll;
			if (cy + CARD_H < ly || cy > ly + lh) continue;

			boolean hover = inList(mouseX, mouseY) && mouseX >= cx && mouseX < cx + cw && mouseY >= cy && mouseY < cy + CARD_H;
			ctx.fill(cx, cy, cx + cw, cy + CARD_H, hover ? CARD_HOVER : CARD_BG);
			if (e == selected) ctx.drawBorder(cx, cy, cw, CARD_H, accent);

			String name = e instanceof HudModule m ? m.name : "Theme";
			name = textRenderer.trimToWidth(name, cw - 6);
			ctx.drawText(textRenderer, name, cx + (cw - textRenderer.getWidth(name)) / 2, cy + 11, 0xFFFFFFFF, false);

			int stripY = cy + CARD_H - 14;
			if (e instanceof HudModule m) {
				ctx.fill(cx + 3, stripY, cx + cw - 3, stripY + 11, m.enabled ? accent : OFF);
				String s = m.enabled ? "ENABLED" : "DISABLED";
				ctx.drawText(textRenderer, s, cx + (cw - textRenderer.getWidth(s)) / 2, stripY + 2,
						m.enabled ? 0xFFFFFFFF : MUTED, false);
			} else {
				ctx.fill(cx + 3, stripY, cx + cw - 3, stripY + 11, OFF);
				String s = "COLORS";
				ctx.drawText(textRenderer, s, cx + (cw - textRenderer.getWidth(s)) / 2, stripY + 2, accent, false);
			}
		}
		ctx.disableScissor();

		// Scrollbar
		int content = contentHeight();
		if (content > lh) {
			int barH = Math.max(12, lh * lh / content);
			int barY = ly + (lh - barH) * scroll / (content - lh);
			ctx.fill(lx + lw + 2, barY, lx + lw + 4, barY + barH, 0x80FFFFFF);
		}
	}

	private boolean inList(double mx, double my) {
		return mx >= lx && mx < lx + lw && my >= ly && my < ly + lh;
	}

	// ---------------------------------------------------------------- input

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		for (Control c : controls) {
			if (c.hover(mx, my) && c.click(mx, my)) {
				if (c instanceof Slider) dragging = c;
				return true;
			}
		}
		if (button == 0 && inList(mx, my)) {
			List<Object> list = entries();
			int cols = columns();
			int cw = cardWidth();
			for (int i = 0; i < list.size(); i++) {
				int cx = lx + (i % cols) * (cw + GAP);
				int cy = ly + (i / cols) * (CARD_H + GAP) - scroll;
				if (mx < cx || mx >= cx + cw || my < cy || my >= cy + CARD_H) continue;
				Object e = list.get(i);
				if (e instanceof HudModule m && my >= cy + CARD_H - 14) {
					m.enabled = !m.enabled;
				}
				if (selected != e) {
					selected = e;
					colorTab = 0;
				}
				rebuild();
				return true;
			}
		}
		return super.mouseClicked(mx, my, button);
	}

	@Override
	public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
		if (dragging != null) {
			dragging.drag(mx);
			return true;
		}
		return super.mouseDragged(mx, my, button, dx, dy);
	}

	@Override
	public boolean mouseReleased(double mx, double my, int button) {
		dragging = null;
		return super.mouseReleased(mx, my, button);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
		if (inList(mx, my)) {
			scroll = MathHelper.clamp(scroll - (int) (vertical * 16), 0, Math.max(0, contentHeight() - lh));
			return true;
		}
		return super.mouseScrolled(mx, my, horizontal, vertical);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (OrbitHud.menuKey.matchesKey(keyCode, scanCode)) {
			close();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		HudConfig.save();
		super.close();
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	// ---------------------------------------------------------------- controls

	private abstract class Control {
		final int x, y, w, h;

		Control(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		boolean hover(double mx, double my) {
			return mx >= x && mx < x + w && my >= y && my < y + h;
		}

		abstract void render(DrawContext ctx, int mx, int my);

		boolean click(double mx, double my) {
			return false;
		}

		void drag(double mx) {}
	}

	private class Button extends Control {
		final String label;
		final BooleanSupplier active;
		final Runnable action;

		Button(int x, int y, int w, int h, String label, BooleanSupplier active, Runnable action) {
			super(x, y, w, h);
			this.label = label;
			this.active = active;
			this.action = action;
		}

		@Override
		void render(DrawContext ctx, int mx, int my) {
			boolean on = active.getAsBoolean();
			int bg = on ? OrbitHud.accent : hover(mx, my) ? 0xFF33333D : 0xFF26262E;
			ctx.fill(x, y, x + w, y + h, bg);
			String s = textRenderer.trimToWidth(label, w - 4);
			ctx.drawText(textRenderer, s, x + (w - textRenderer.getWidth(s)) / 2, y + (h - 8) / 2,
					on ? 0xFFFFFFFF : 0xFFDDDDDD, false);
		}

		@Override
		boolean click(double mx, double my) {
			action.run();
			return true;
		}
	}

	private class Toggle extends Control {
		final String label;
		final BooleanSupplier get;
		final Runnable flip;

		Toggle(int x, int y, int w, String label, BooleanSupplier get, Runnable flip) {
			super(x, y, w, 12);
			this.label = label;
			this.get = get;
			this.flip = flip;
		}

		@Override
		void render(DrawContext ctx, int mx, int my) {
			boolean on = get.getAsBoolean();
			ctx.drawText(textRenderer, textRenderer.trimToWidth(label, w - 24), x, y + 2,
					hover(mx, my) ? 0xFFFFFFFF : 0xFFCCCCCC, false);
			int tx = x + w - 18;
			ctx.fill(tx, y + 2, tx + 18, y + 10, on ? OrbitHud.accent : OFF);
			int knob = on ? tx + 11 : tx + 1;
			ctx.fill(knob, y + 3, knob + 6, y + 9, 0xFFFFFFFF);
		}

		@Override
		boolean click(double mx, double my) {
			flip.run();
			return true;
		}
	}

	private class Slider extends Control {
		final String label;
		final double min, max;
		final DoubleSupplier get;
		final DoubleConsumer set;
		final java.util.function.DoubleFunction<String> format;
		int trackColor = 0xFFDDDDDD;

		Slider(int x, int y, int w, String label, double min, double max, DoubleSupplier get, DoubleConsumer set,
		       java.util.function.DoubleFunction<String> format) {
			super(x, y, w, 16);
			this.label = label;
			this.min = min;
			this.max = max;
			this.get = get;
			this.set = set;
			this.format = format;
		}

		Slider tint(int shift) {
			trackColor = switch (shift) {
				case 16 -> 0xFFFF5555;
				case 8 -> 0xFF55FF77;
				case 0 -> 0xFF5599FF;
				default -> 0xFFBBBBBB;
			};
			return this;
		}

		@Override
		void render(DrawContext ctx, int mx, int my) {
			double v = get.getAsDouble();
			ctx.drawText(textRenderer, label, x, y, 0xFFCCCCCC, false);
			String val = format.apply(v);
			ctx.drawText(textRenderer, val, x + w - textRenderer.getWidth(val), y, MUTED, false);

			int ty = y + 11;
			double t = (v - min) / (max - min);
			int filled = x + (int) Math.round(w * t);
			ctx.fill(x, ty, x + w, ty + 3, 0xFF2E2E38);
			ctx.fill(x, ty, filled, ty + 3, trackColor);
			int knobX = MathHelper.clamp(filled - 2, x, x + w - 4);
			ctx.fill(knobX, ty - 2, knobX + 4, ty + 5, 0xFFFFFFFF);
		}

		@Override
		boolean click(double mx, double my) {
			drag(mx);
			return true;
		}

		@Override
		void drag(double mx) {
			double t = MathHelper.clamp((mx - x) / w, 0, 1);
			set.accept(min + t * (max - min));
		}
	}
}
