package org.k4n3d4.PlayerLog;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.render.EventRender3D;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.client.api.utils.WorldUtils;
import org.rusherhack.core.event.subscribe.Subscribe;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import org.rusherhack.core.notification.NotificationType;
import org.rusherhack.core.setting.BooleanSetting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PlayerLogModule extends ToggleableModule {
	private final BooleanSetting logInChat = new BooleanSetting("Log in chat", true);
	private final BooleanSetting logInFile = new BooleanSetting("Log in file", true);
	private final BooleanSetting logInNotification = new BooleanSetting("Notification", true);
	private final BooleanSetting playASound = new BooleanSetting("Play a sound", true);
	private final BooleanSetting ignoreFriend = new BooleanSetting("Ignore friend", true);
	private final BooleanSetting filerNPC = new BooleanSetting("Filter NPC", "WARNING activating this option will disable the plugin on 2b2t and some servers", false);

	// Log file path
	private static final String logFilePath = "rusherhack/logs/player_log.txt";

	public PlayerLogModule() {
		super("PlayerLog", "Log all player encounters", ModuleCategory.CLIENT);

		this.registerSettings(
				logInChat,
				logInFile,
				logInNotification,
				playASound,
				ignoreFriend,
				filerNPC
		);
	}

	public static String getLogFilePath() {
		return logFilePath;
	}

	private final Set<UUID> playersInView = new HashSet<>();

	@Subscribe
	private void onRender3D(EventRender3D event) {
		Set<UUID> currentlyVisible = new HashSet<>();

		for (Entity entity : WorldUtils.getEntities()) {
			if (entity instanceof Player player && player != Minecraft.getInstance().player) {

				// Ignore self
				Player localPlayer = Minecraft.getInstance().player;
				if (player.getUUID().equals(localPlayer.getUUID())) {
					continue;
				}

				if (filerNPC.getValue()) {
					// Ignore fakeplayer by checking if it has a connection
					if (Minecraft.getInstance().getConnection().getPlayerInfo(player.getUUID()) == null) {
						continue;
					}

					// Ignore fakeplayer by checking if it has a ping equal to 0
					int ping = Minecraft.getInstance().getConnection().getPlayerInfo(player.getUUID()).getLatency();
					if (ping == 0) {
						continue;
					}
				}

				UUID id = player.getUUID();
				currentlyVisible.add(id);

				if (ignoreFriend.getValue() && RusherHackAPI.getRelationManager().isFriend(player.getName().getString())) {
					continue;
				}

				if (!playersInView.contains(id)) {
					// Get date and time
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					String time = LocalDateTime.now().format(formatter);

					// get server IP
					String serverIp = "Singleplayer";
					if (Minecraft.getInstance().getCurrentServer() != null) {
						serverIp = Minecraft.getInstance().getCurrentServer().ip;
					}

					// Get position
					Vec3 pos = player.position();
					int x = (int) pos.x;
					int y = (int) pos.y;
					int z = (int) pos.z;

					// Get dimension
					String dimension = player.level().dimension().location().toString().replace("minecraft:", "");

					// Log info
					String logMessage = "[" + time + "] [" + serverIp + "] <" + player.getName().getString() + "> seen at [X=" + x + "] [Y=" + y + "] [Z=" + z + "] in the " + dimension;
					if (logInChat.getValue()) {
						ChatUtils.print(logMessage);
					}
					if (logInNotification.getValue()) {
						RusherHackAPI.getNotificationManager().send(NotificationType.INFO, logMessage);
					}
					if (logInFile.getValue()) {
						logToFile(logMessage);
					}
					if (playASound.getValue()) {
						mc.execute(() -> mc.level.playLocalSound(
								mc.player.getX(),
								mc.player.getY(),
								mc.player.getZ(),
								SoundEvents.EXPERIENCE_ORB_PICKUP,
								SoundSource.PLAYERS,
								1.0F,
								1.0F,
								false
						));
					}
					playersInView.add(id);
				}
			}
		}

		playersInView.retainAll(currentlyVisible);
	}

	public void logToFile(String message) {
		try {
			// Choose .minecraft/rusherhack/logs/player_log.txt as the log file
			File logFile = new File(Minecraft.getInstance().gameDirectory, logFilePath);

			// Check and create the folder if it does not exist
			if (!logFile.getParentFile().exists()) {
				logFile.getParentFile().mkdirs();
			}

			// Check and create the file if it does not exist
			if (!logFile.exists()) {
				logFile.createNewFile();
			}

			// Read existing content
			StringBuilder oldContent = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(logFile));
			String line;
			while ((line = reader.readLine()) != null) {
				oldContent.append(line).append(System.lineSeparator());
			}
			reader.close();

			// Write log at the begining of the file
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
			writer.write(message);
			writer.newLine();
			writer.write(oldContent.toString());
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
