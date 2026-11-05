package com.vpapirian.orbithud;

import com.vpapirian.orbithud.config.HudConfig;
import com.vpapirian.orbithud.gui.HudEditorScreen;
import com.vpapirian.orbithud.gui.ModMenuScreen;
import com.vpapirian.orbithud.hud.ModuleManager;
import com.vpapirian.orbithud.hud.modules.TargetHealthModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrbitHud implements ClientModInitializer {
	public static final String MOD_ID = "orbithud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Accent color used by the mod menu and editor (ARGB). */
	public static int accent = 0xFF7C5CFF;

	public static KeyBinding menuKey;

	@Override
	public void onInitializeClient() {
		ModuleManager.init();
		HudConfig.load();

		menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.orbithud.menu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				"category.orbithud"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (menuKey.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new ModMenuScreen());
				}
			}
			ModuleManager.tick();
		});

		HudRenderCallback.EVENT.register((context, tickCounter) -> {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc.options.hudHidden || mc.player == null) return;
			// The editor draws modules itself so they can be dragged.
			if (mc.currentScreen instanceof HudEditorScreen) return;
			// Show placeholder data while the menu is open so color changes are visible live.
			ModuleManager.render(context, mc.currentScreen instanceof ModMenuScreen);
		});

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (world.isClient) TargetHealthModule.onAttack(entity);
			return ActionResult.PASS;
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> HudConfig.save());
	}
}
