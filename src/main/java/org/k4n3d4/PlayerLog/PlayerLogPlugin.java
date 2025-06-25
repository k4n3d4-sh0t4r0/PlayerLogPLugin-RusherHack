package org.k4n3d4.PlayerLog;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

public class PlayerLogPlugin extends Plugin {
	
	@Override
	public void onLoad() {
		//creating and registering a new module
		final PlayerLogModule playerLogModule = new PlayerLogModule();
		RusherHackAPI.getModuleManager().registerFeature(playerLogModule);

		PlayerLogWindows logWindows = new PlayerLogWindows();
		RusherHackAPI.getWindowManager().registerFeature(logWindows);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("PlayerLog plugin unloaded!");
	}
	
}