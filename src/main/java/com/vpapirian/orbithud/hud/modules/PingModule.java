package com.vpapirian.orbithud.hud.modules;

import com.vpapirian.orbithud.hud.TextModule;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

public class PingModule extends TextModule {
	public PingModule() {
		super("ping", "Ping", 4, 130);
	}

	@Override
	protected String text(boolean preview) {
		int ping = 0;
		ClientPlayNetworkHandler handler = mc.getNetworkHandler();
		if (handler != null && mc.player != null) {
			PlayerListEntry entry = handler.getPlayerListEntry(mc.player.getUuid());
			if (entry != null) ping = entry.getLatency();
		}
		return "Ping: " + ping + " ms";
	}
}
