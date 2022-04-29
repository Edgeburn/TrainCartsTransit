package com.edgeburnmedia.traincartstransit.managers;

import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import com.edgeburnmedia.traincartstransit.stop.StopInfo;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class StopInfoManager {
	private final TrainCartsTransit plugin;
	private FileConfiguration fileConfiguration;
	private File configFile;
	private ArrayList<StopInfo> stops = new ArrayList<>();

	public StopInfoManager(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}


	public void registerStop(StopInfo stop) {
		stops.add(stop);
	}

	public void removeEntry(StopInfo stopInfo) {
		stops.remove(stopInfo);
		fileConfiguration.set(stopInfo.getDestinationName(), null);
		try {
			fileConfiguration.save(configFile);
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to save removal of stop " + stopInfo.getDestinationName() + "from config file!");
		}
	}

	public void saveStopEntry(StopInfo stop) {
		fileConfiguration.set(stop.getDestinationName() + ".sound", stop.getSoundId());
		fileConfiguration.set(stop.getDestinationName() + ".displayname", stop.getNextStopDisplayName());
		try {
			fileConfiguration.save(configFile);
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to save config file!");
		}
	}

	public StopInfo getStopInfo(String destination) {
//		Bukkit.broadcastMessage("destination is " + destination);
		for (StopInfo stopInfo : stops) {
			if (stopInfo.getDestinationName().equalsIgnoreCase(destination)) {
//				Bukkit.broadcastMessage("got " + stopInfo.getDestinationName() + ", " + stopInfo.getNextStopDisplayName());
				return stopInfo;
			}
		}
		return null;
	}

	public String getSound(String destination) {
		return getStopInfo(destination).getSoundId();
	}

	public String getDisplayName(String destination) {
		StopInfo stop = getStopInfo(destination);
		if (stop != null) {
			return stop.getNextStopDisplayName();
		} else {
			return "Unknown";
		}
	}


	public void saveConfigFile() {
		configFile = new File(plugin.getDataFolder(), "stops.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			plugin.saveResource("stops.yml", false);
		}

		fileConfiguration = new YamlConfiguration();
		try {
			fileConfiguration.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			plugin.getLogger().severe("Failed to save stops.yml!");
		}
	}

	public void readConfigFile() {
		Set<String> keys = fileConfiguration.getKeys(false);

		keys.forEach(s -> {
			StopInfo stopInfo = new StopInfo();
			stopInfo.setDestinationName(s);
			stopInfo.setNextStopDisplayName(fileConfiguration.getString(s + ".displayname"));
			stopInfo.setSoundId(fileConfiguration.getString(s + ".sound"));
			registerStop(stopInfo);
		});
	}
}
