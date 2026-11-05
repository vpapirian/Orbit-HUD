package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.HudModule;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.Heightmap;

/**
 * Top-down terrain map, one pixel per block, colored like vanilla maps.
 * Rebuilt every few ticks into a dynamic texture, then drawn scaled.
 */
public class MinimapModule extends HudModule {
	private static final int TEXTURE_SIZE = 128; // blocks across
	private static final int DISPLAY_SIZE = 96;  // gui pixels at scale 1
	private static final int UPDATE_EVERY = 5;   // ticks

	private final Identifier textureId = Identifier.of("orbithud", "minimap");
	private NativeImageBackedTexture texture;
	private int ticks;

	public MinimapModule() {
		super("minimap", "Minimap", 10000, 4);
		backgroundColor = 0xC0000000;
		width = DISPLAY_SIZE + 4;
		height = DISPLAY_SIZE + 4;
		defineFlag("Show compass", true);
		defineFlag("Show border", true);
	}

	@Override
	public void tick() {
		if (mc.world == null || mc.player == null) return;
		if (texture == null) {
			texture = new NativeImageBackedTexture(TEXTURE_SIZE, TEXTURE_SIZE, true);
			mc.getTextureManager().registerTexture(textureId, texture);
		}
		if (ticks++ % UPDATE_EVERY == 0) rebuild();
	}

	private void rebuild() {
		NativeImage img = texture.getImage();
		if (img == null) return;
		ClientWorld world = mc.world;
		BlockPos.Mutable pos = new BlockPos.Mutable();
		int originX = mc.player.getBlockX() - TEXTURE_SIZE / 2;
		int originZ = mc.player.getBlockZ() - TEXTURE_SIZE / 2;

		for (int ix = 0; ix < TEXTURE_SIZE; ix++) {
			for (int iz = 0; iz < TEXTURE_SIZE; iz++) {
				int wx = originX + ix;
				int wz = originZ + iz;
				if (!world.getChunkManager().isChunkLoaded(wx >> 4, wz >> 4)) {
					img.setColor(ix, iz, 0xFF000000);
					continue;
				}
				int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, wx, wz) - 1;
				pos.set(wx, y, wz);
				BlockState state = world.getBlockState(pos);
				MapColor mapColor = state.getMapColor(world, pos);

				// Shade by comparing to the block to the north, like vanilla maps.
				int northY = world.getTopY(Heightmap.Type.WORLD_SURFACE, wx, wz - 1) - 1;
				MapColor.Brightness b = y > northY ? MapColor.Brightness.HIGH
						: y < northY ? MapColor.Brightness.LOW
						: MapColor.Brightness.NORMAL;

				// getRenderColor returns ABGR, which is what NativeImage expects in 1.21.1.
				int abgr = mapColor == MapColor.CLEAR ? 0xFF000000 : mapColor.getRenderColor(b);
				img.setColor(ix, iz, abgr);
			}
		}
		texture.upload();
	}

	@Override
	protected void render(DrawContext ctx, boolean preview) {
		width = DISPLAY_SIZE + 4;
		height = DISPLAY_SIZE + 4;
		ctx.fill(0, 0, width, height, backgroundColor);

		if (texture != null) {
			ctx.drawTexture(textureId, 2, 2, DISPLAY_SIZE, DISPLAY_SIZE,
					0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
		}
		if (flag("Show border")) ctx.drawBorder(0, 0, width, height, color());

		if (mc.player != null) {
			// Player arrow, rotated to face where the player looks (north is up).
			MatrixStack m = ctx.getMatrices();
			m.push();
			m.translate(2 + DISPLAY_SIZE / 2f, 2 + DISPLAY_SIZE / 2f, 0);
			m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(mc.player.getYaw() + 180f));
			int arrow = 0xFFFF4040;
			ctx.fill(-1, -4, 1, -2, arrow);
			ctx.fill(-2, -2, 2, 0, arrow);
			ctx.fill(-3, 0, 3, 2, arrow);
			ctx.fill(-3, 2, -1, 3, arrow);
			ctx.fill(1, 2, 3, 3, arrow);
			m.pop();
		}

		if (flag("Show compass")) {
			String n = "N";
			ctx.drawText(mc.textRenderer, n, 2 + (DISPLAY_SIZE - mc.textRenderer.getWidth(n)) / 2, 4, color(), true);
		}
	}
}
