package com.vpapirian.orbithud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vpapirian.orbithud.OrbitHud;
import com.vpapirian.orbithud.hud.HudModule;
import com.vpapirian.orbithud.hud.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Saves everything to config/orbithud.json. */
public final class HudConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private HudConfig() {}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("orbithud.json");
	}

	static class ModuleData {
		boolean enabled;
		int x, y;
		int textColor, backgroundColor;
		boolean chroma;
		float scale;
		Map<String, Boolean> flags;
	}

	static class Data {
		int accent;
		Map<String, ModuleData> modules = new LinkedHashMap<>();
	}

	public static void load() {
		Path p = path();
		if (!Files.exists(p)) return;
		try (Reader reader = Files.newBufferedReader(p)) {
			Data data = GSON.fromJson(reader, Data.class);
			if (data == null) return;
			if (data.accent != 0) OrbitHud.accent = data.accent;
			if (data.modules == null) return;

			for (HudModule m : ModuleManager.MODULES) {
				ModuleData md = data.modules.get(m.id);
				if (md == null) continue;
				m.enabled = md.enabled;
				m.x = md.x;
				m.y = md.y;
				m.textColor = md.textColor;
				m.backgroundColor = md.backgroundColor;
				m.chroma = md.chroma;
				if (md.scale > 0) m.scale = md.scale;
				if (md.flags != null) {
					md.flags.forEach((k, v) -> {
						if (m.flags.containsKey(k) && v != null) m.flags.put(k, v);
					});
				}
			}
		} catch (Exception e) {
			OrbitHud.LOGGER.warn("Could not read orbithud.json, using defaults", e);
		}
	}

	public static void save() {
		Data data = new Data();
		data.accent = OrbitHud.accent;
		for (HudModule m : ModuleManager.MODULES) {
			ModuleData md = new ModuleData();
			md.enabled = m.enabled;
			md.x = m.x;
			md.y = m.y;
			md.textColor = m.textColor;
			md.backgroundColor = m.backgroundColor;
			md.chroma = m.chroma;
			md.scale = m.scale;
			md.flags = new LinkedHashMap<>(m.flags);
			data.modules.put(m.id, md);
		}
		try {
			Files.createDirectories(path().getParent());
			try (Writer writer = Files.newBufferedWriter(path())) {
				GSON.toJson(data, writer);
			}
		} catch (Exception e) {
			OrbitHud.LOGGER.warn("Could not save orbithud.json", e);
		}
	}
}
