package com.vpapirian.orbithud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class for every HUD element. Subclasses draw at (0, 0) in {@link #render}
 * and set {@link #width}/{@link #height}; position and scale are applied here.
 */
public abstract class HudModule {
	protected static final MinecraftClient mc = MinecraftClient.getInstance();

	/** Sentinel: center on this axis until the player moves it. */
	public static final int CENTER = -1;

	public final String id;
	public final String name;

	public boolean enabled = true;
	public int x, y;
	public int textColor = 0xFFFFFFFF;
	public int backgroundColor = 0x70000000;
	public boolean chroma = false;
	public float scale = 1.0f;

	/** Per-module on/off options, shown as toggles in the mod menu and saved to config. */
	public final Map<String, Boolean> flags = new LinkedHashMap<>();

	protected int width = 40;
	protected int height = 16;

	/** Where the module was actually drawn last frame (after centering/clamping). */
	public int renderX, renderY;

	protected HudModule(String id, String name, int x, int y) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
	}

	/** Called every client tick while enabled. */
	public void tick() {}

	/**
	 * Draw the module at (0, 0). When {@code preview} is true (mod menu / layout editor),
	 * draw placeholder data if there is nothing real to show.
	 */
	protected abstract void render(DrawContext ctx, boolean preview);

	public final void draw(DrawContext ctx, boolean preview, int atX, int atY) {
		renderX = atX;
		renderY = atY;
		MatrixStack m = ctx.getMatrices();
		m.push();
		m.translate(atX, atY, 0);
		m.scale(scale, scale, 1f);
		render(ctx, preview);
		m.pop();
	}

	/** Text color, with chroma applied. */
	public int color() {
		return chroma ? ColorUtil.chroma(textColor) : textColor;
	}

	public int getScaledWidth() {
		return Math.max(1, Math.round(width * scale));
	}

	public int getScaledHeight() {
		return Math.max(1, Math.round(height * scale));
	}

	public boolean flag(String key) {
		return flags.getOrDefault(key, false);
	}

	protected void defineFlag(String key, boolean defaultValue) {
		flags.putIfAbsent(key, defaultValue);
	}

	protected void drawBackground(DrawContext ctx) {
		if (!flag("Hide background")) ctx.fill(0, 0, width, height, backgroundColor);
	}
}
