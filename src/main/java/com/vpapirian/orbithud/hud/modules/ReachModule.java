package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.ReachMath;
import com.vpapirian.orbithud.hud.TextModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

/** Distance from your eyes to the last entity you hit. Display only. */
public class ReachModule extends TextModule {
	private static final long HOLD_MS = 5000;

	private static double lastReach = -1;
	private static long lastTime;

	public ReachModule() {
		super("reach", "Reach", 4, 187);
	}

	public static void onAttack(PlayerEntity player, Entity entity) {
		if (!(entity instanceof LivingEntity)) return;
		Vec3d eye = player.getEyePos();
		Box box = entity.getBoundingBox();
		lastReach = ReachMath.distanceToBox(eye.x, eye.y, eye.z,
				box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
		lastTime = System.currentTimeMillis();
	}

	@Override
	protected String text(boolean preview) {
		if (preview) return "Reach: 2.94 blocks";
		boolean fresh = lastReach >= 0 && System.currentTimeMillis() - lastTime < HOLD_MS;
		return fresh ? String.format(Locale.ROOT, "Reach: %.2f blocks", lastReach) : "Reach: -";
	}
}
