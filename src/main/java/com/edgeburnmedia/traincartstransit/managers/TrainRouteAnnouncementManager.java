package com.edgeburnmedia.traincartstransit.managers;

import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class TrainRouteAnnouncementManager {
	private final TrainCartsTransit plugin;
	private FileConfiguration fileConfiguration;
	private File configFile;
	private HashMap<String, String> soundsMap = new HashMap<>();

	public TrainRouteAnnouncementManager(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}

	/**
	 * Register the sound to be played when a given train route
	 *
	 * @param routeID The route ID for the train route
	 * @param soundID The sound id for the train's route announcement sound
	 */
	public void registerSound(String routeID, String soundID) {
		soundsMap.put(routeID, soundID);
	}

	public void removeEntry(String routeID) {
		soundsMap.remove(routeID);
		fileConfiguration.set(routeID, null);
		try {
			fileConfiguration.save(configFile);
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to save removal of " + routeID + "from config file!");
		}
	}

	public void saveSoundEntry(String routeID, String soundID) {
		fileConfiguration.set(routeID, soundID);
		try {
			fileConfiguration.save(configFile);
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to save config file!");
		}
	}

	public String getSound(String routeID) {
		return soundsMap.get(routeID);
	}

	public void saveConfigFile() {
		configFile = new File(plugin.getDataFolder(), "route_announcements.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			plugin.saveResource("route_announcements.yml", false);
		}

		fileConfiguration = new YamlConfiguration();
		try {
			fileConfiguration.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			plugin.getLogger().severe("Failed to save route_announcements.yml!");
		}
	}

	public void readConfigFile() {
		Set<String> keys = fileConfiguration.getKeys(false);

		keys.forEach(s -> {
			registerSound(s, fileConfiguration.getString(s));
		});
	}


}
