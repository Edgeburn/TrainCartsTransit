package com.edgeburnmedia.traincartstransit.managers;

import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import com.edgeburnmedia.traincartstransit.stop.StopDisplayName;
import com.edgeburnmedia.traincartstransit.stop.StopInfo;
import java.util.Objects;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

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

	public @Nullable StopInfo getStopInfo(String destination) {
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
		return Objects.requireNonNullElseGet(getStopInfo(destination), () -> {
			StopInfo stopInfo = new StopInfo();
			stopInfo.setDestinationName(destination);
			return stopInfo;
		}).getSoundId();
	}

	public StopDisplayName getDisplayName(String destination) {
		StopInfo stop = getStopInfo(destination);
		if (stop != null) {
			return stop.getNextStopDisplayName();
		} else {
			return new StopDisplayName(destination);
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
		try {
			fileConfiguration.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}
		stops.clear();
		Set<String> keys = fileConfiguration.getKeys(false);

		keys.forEach(s -> {
			StopInfo stopInfo = new StopInfo();
			stopInfo.setDestinationName(s);
			List<String> displayName = fileConfiguration.getStringList(s + ".displayname");
			if (displayName.size() != 0) {
				stopInfo.setNextStopDisplayName(displayName);
			} else {
				String legacyDisplayName = fileConfiguration.getString(s + ".displayname"); // support display name as a single string rather than a list
				if (legacyDisplayName != null) {
					stopInfo.setNextStopDisplayName(Collections.singletonList(legacyDisplayName));
				} else {
					stopInfo.setNextStopDisplayName(StopDisplayName.unknownStop());
				}
			}
			stopInfo.setSoundId(fileConfiguration.getString(s + ".sound"));
			registerStop(stopInfo);
		});
	}
}
