package com.edgeburnmedia.traincartstransit.properties;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.ITrainProperty;
import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RouteID implements ITrainProperty<String>, CommandExecutor, TabCompleter {
	private final TrainCartsTransit plugin;

	public RouteID(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}

	/**
	 * Get a {@link List<String>} of the names of all trains that exist.
	 *
	 * @return The list of names of trains that currently exist.
	 */
	public static List<String> getNamesOfExistingTrains() {
		Collection<TrainProperties> savedTrains = TrainProperties.getAll();
		List<String> savedTrainNames = new ArrayList<>();
		savedTrains.forEach(cartProperties -> {
			savedTrainNames.add(cartProperties.getTrainName());
		});
		return savedTrainNames;
	}

	public static List<TrainProperties> getTrainsExisting() {
		Collection<TrainProperties> trainPropertiesCollection = TrainProperties.getAll();
		return List.copyOf(trainPropertiesCollection);
	}

	@CommandMethod("traincartstransit|trt setroute <routeid> <trainname>")
	@CommandDescription("Set the route ID of the train")
	public void setProperty(final CommandSender sender, /*TrainProperties properties,*/ final @Argument("routeid") String routeid, @Argument("trainname") String trainname) {
		TrainProperties.get(trainname).set(this, routeid);
		sendPropertyCommand(sender, trainname);
	}

	public void sendPropertyCommand(final CommandSender sender, final @Argument("trainname") String trainname) {
		sender.sendMessage(ChatColor.YELLOW + "Route ID is " + ChatColor.WHITE + getTrainRouteID(trainname));
	}

	public String getTrainRouteID(String trainname) {
		TrainProperties properties = TrainProperties.get(trainname);
		return getTrainRouteID(properties);
	}

	public String getTrainRouteID(TrainProperties properties) {
		return properties.get(this);
	}

	/**
	 * Gets the default value of this property. If the current value of the
	 * property equals this value, then the property is not written to the
	 * configuration. As well, this default is used during initialization
	 * of new properties.
	 *
	 * @return default property value
	 */
	@Override
	public String getDefault() {
		return "UNKNOWN";
	}

	/**
	 * Tries to to read the value of this property from a given YAML configuration.
	 * The configuration could be of a train, cart or train default. If the value
	 * is not stored in the configuration, {@link Optional#empty()} should be returned.<br>
	 * <br>
	 * If the property is unset because it is the default value, then empty needs to be
	 * returned anyway. Applying the default in this situation is done automatically.
	 *
	 * @param config YAML configuration from which to read
	 * @return read value, or {@link Optional#empty()} if none was read
	 */
	@Override
	public Optional<String> readFromConfig(ConfigurationNode config) {
		return Util.getConfigOptional(config, "routeid", String.class);
	}

	/**
	 * Updates the value of this property in the given YAML configuration.
	 * The configuration could be of a train, cart or train default. If the value
	 * is {@link Optional#isPresent()}, the value should be written to the configuration.
	 * If it is not present, the relevant configuration entries need to be
	 * removed from the configuration.<br>
	 * <br>
	 * This method is generally called with {@link Optional#empty()} when the current
	 * value is the default one.
	 *
	 * @param config YAML configuration to which to write
	 * @param value  The value to write when {@link Optional#isPresent()}, otherwise
	 *               the property should be removed from the configuration node
	 */
	@Override
	public void writeToConfig(ConfigurationNode config, Optional<String> value) {
		Util.setConfigOptional(config, "routeid", value);
	}

	/**
	 * Executes the given command, returning its success.
	 * <br>
	 * If false is returned, then the "usage" plugin.yml entry for this command
	 * (if defined) will be sent to the player.
	 *
	 * @param sender  Source of the command
	 * @param command Command which was executed
	 * @param label   Alias of the command which was used
	 * @param args    Passed command arguments
	 * @return true if a valid command, otherwise false
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		switch (args[0]) {
			case "setroute":
				if (args.length == 3) {
					String trainName = args[1];
					String newRouteID = args[2];
					TrainProperties prop = TrainProperties.get(trainName);
					prop.set(this, newRouteID);
					sendPropertyCommand(sender, trainName);
				} else {
					sender.sendMessage(ChatColor.YELLOW + "Usage: /trt setroute <trainname> <routeid>");
				}
				break;
			case "getroute":
				if (args.length == 2) {
					String trainName = args[1];
					sendPropertyCommand(sender, trainName);
				} else {
					sender.sendMessage(ChatColor.YELLOW + "Usage: /trt getroute <trainname>");
				}
				break;
			case "setroutesound":
				if (args.length == 3) {
					String route = args[1];
					String sound = args[2];
					plugin.getAnnouncementManager().registerSound(route, sound);
					plugin.getAnnouncementManager().saveSoundEntry(route, sound);
					return true;
				} else {
					sender.sendMessage(ChatColor.YELLOW + "Usage: /trt setroutesound <routeid> <soundid>");
				}
				break;
			default:
				return false;
		}
		return true;
	}

	/**
	 * Requests a list of possible completions for a command argument.
	 *
	 * @param sender  Source of the command.  For players tab-completing a
	 *                command inside of a command block, this will be the player, not
	 *                the command block.
	 * @param command Command which was executed
	 * @param label   Alias of the command which was used
	 * @param args    The arguments passed to the command, including final
	 *                partial argument to be completed
	 * @return A List of possible completions for the final argument, or null
	 * to default to the command executor
	 */
	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		List<String> commands = new ArrayList<>();
		List<String> completions = new ArrayList<>();

		if (args.length == 1) {
			commands.add("setroute");
			commands.add("getroute");
			commands.add("setroutesound");
			commands.add("stop");
			StringUtil.copyPartialMatches(args[0], commands, completions);
		} else if (args.length == 2 && (args[0].equalsIgnoreCase("setroute") || args[0].equalsIgnoreCase("getroute"))) {
			commands.addAll(getNamesOfExistingTrains());
			StringUtil.copyPartialMatches(args[1], commands, completions);
		}

		Collections.sort(completions);
		return completions;
	}
}