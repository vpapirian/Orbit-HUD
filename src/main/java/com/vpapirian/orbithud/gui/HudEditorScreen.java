package com.vpapirian.orbithud.gui;

import com.vpapirian.orbithud.OrbitHud;
import com.vpapirian.orbithud.config.HudConfig;
import com.vpapirian.orbithud.hud.HudModule;
import com.vpapirian.orbithud.hud.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

/** Drag modules to move them, scroll over one to resize it. Snaps to edges and center. */
public class HudEditorScreen extends Screen {
	private static final int SNAP = 4;

	private final Screen parent;
	private HudModule dragging;
	private int offsetX, offsetY;
	private boolean snappedX, snappedY;

	public HudEditorScreen(Screen parent) {
		super(Text.literal("Edit HUD Layout"));
		this.parent = parent;
	}

	@Override
	public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
		ctx.fill(0, 0, width, height, 0x30000000);
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		int accent = OrbitHud.accent;

		if (dragging != null) {
			if (snappedX) ctx.fill(width / 2, 0, width / 2 + 1, height, 0x60FFFFFF);
			if (snappedY) ctx.fill(0, height / 2, width, height / 2 + 1, 0x60FFFFFF);
		}

		ModuleManager.render(ctx, true);

		HudModule hovered = moduleAt(mouseX, mouseY);
		for (HudModule m : ModuleManager.MODULES) {
			if (!m.enabled) continue;
			boolean active = m == dragging || (dragging == null && m == hovered);
			ctx.drawBorder(m.renderX - 1, m.renderY - 1, m.getScaledWidth() + 2, m.getScaledHeight() + 2,
					active ? accent : 0x50FFFFFF);
			if (active) {
				String label = m.name + "  " + Math.round(m.scale * 100) + "%";
				int ly = m.renderY > 11 ? m.renderY - 11 : m.renderY + m.getScaledHeight() + 2;
				ctx.drawText(textRenderer, label, m.renderX, ly, accent, true);
			}
		}

		String hint = "Drag to move  •  Scroll to resize  •  Esc to go back";
		ctx.drawText(textRenderer, hint, (width - textRenderer.getWidth(hint)) / 2, height - 46, 0xFFFFFFFF, true);
	}

	private HudModule moduleAt(double mx, double my) {
		List<HudModule> list = ModuleManager.MODULES;
		for (int i = list.size() - 1; i >= 0; i--) {
			HudModule m = list.get(i);
			if (!m.enabled) continue;
			if (mx >= m.renderX && mx < m.renderX + m.getScaledWidth()
					&& my >= m.renderY && my < m.renderY + m.getScaledHeight()) return m;
		}
		return null;
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		HudModule m = moduleAt(mx, my);
		if (m != null && button == 0) {
			dragging = m;
			offsetX = (int) mx - m.renderX;
			offsetY = (int) my - m.renderY;
			return true;
		}
		return super.mouseClicked(mx, my, button);
	}

	@Override
	public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
		if (dragging == null) return super.mouseDragged(mx, my, button, dx, dy);
		int w = dragging.getScaledWidth();
		int h = dragging.getScaledHeight();
		int nx = (int) mx - offsetX;
		int ny = (int) my - offsetY;

		snappedX = false;
		snappedY = false;
		if (Math.abs(nx) < SNAP) nx = 0;
		if (Math.abs(nx + w - width) < SNAP) nx = width - w;
		if (Math.abs(nx + w / 2 - width / 2) < SNAP) { nx = width / 2 - w / 2; snappedX = true; }
		if (Math.abs(ny) < SNAP) ny = 0;
		if (Math.abs(ny + h - height) < SNAP) ny = height - h;
		if (Math.abs(ny + h / 2 - height / 2) < SNAP) { ny = height / 2 - h / 2; snappedY = true; }

		dragging.x = MathHelper.clamp(nx, 0, Math.max(0, width - w));
		dragging.y = MathHelper.clamp(ny, 0, Math.max(0, height - h));
		return true;
	}

	@Override
	public boolean mouseReleased(double mx, double my, int button) {
		dragging = null;
		return super.mouseReleased(mx, my, button);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
		HudModule m = moduleAt(mx, my);
		if (m != null) {
			m.scale = MathHelper.clamp(Math.round((m.scale + (float) vertical * 0.05f) * 20) / 20f, 0.5f, 2.0f);
			return true;
		}
		return super.mouseScrolled(mx, my, horizontal, vertical);
	}

	@Override
	public void close() {
		HudConfig.save();
		client.setScreen(parent);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
