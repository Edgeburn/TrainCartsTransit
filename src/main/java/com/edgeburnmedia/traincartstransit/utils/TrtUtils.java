package com.edgeburnmedia.traincartstransit.utils;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import com.edgeburnmedia.traincartstransit.stop.StopDisplayName;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class TrtUtils {

	public static void ringBellOfPlayerTrain(TrainCartsTransit plugin, Player player) {
		Entity vehicle = player.getVehicle();
		MinecartMember<?> member = MinecartMemberStore.getFromEntity(vehicle);
		MinecartGroup train = member.getGroup();
		train.getProperties().set(plugin.getBellRungTrainProperty(), true);
		StopDisplayName nextStopDisplayName = TrtUtils.getNextStopDisplayName(plugin, train);
		TrtUtils.runOnAllPassengers(train, player1 -> {
			player.sendTitle(ChatColor.GOLD + "STOP REQUESTED", ChatColor.GOLD + "Stopping at " + nextStopDisplayName, 20, 70, 20);
			plugin.getTransitTonesPlayer().playBellRungTone(player);
		});
	}

	/**
	 * Checks whether players exist within a certain radius of a {@link Location}
	 *
	 * @param location Location to check
	 * @param range    Radius to check
	 * @return True if players exist, False if not
	 */
	public static boolean arePlayersInRange(Location location, double range, Player... ignored) {
		// FIXME: trains will not stop if a player is waiting at a stop if there are players riding the train
		Collection<Entity> nearbyEntities;
		List<UUID> ignoredPlayerUUIDS = new ArrayList<>();
		for (int i = 0; i < ignored.length; i++) {
			ignoredPlayerUUIDS.add(ignored[i].getUniqueId());
		}
		try {
			nearbyEntities = location.getWorld().getNearbyEntities(location, range, range, range);
		} catch (NullPointerException e) {
			return false;
		}
		if (nearbyEntities.isEmpty()) {
			return false;
		}
		for (Entity entity : nearbyEntities) {
			if (entity instanceof Player) {
				if (!doesListContain(ignoredPlayerUUIDS, entity.getUniqueId())) {
					return true;
				}
			}
		}
		return false;
	}

	public static <T> boolean doesListContain(List<T> list, T check) {
		for (T c : list) {
			if (list.contains(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether players exist within a certain radius of a {@link Block}'s {@link Location}
	 *
	 * @param block Location to check
	 * @param range Radius to check
	 * @return True if players exist, False if not
	 */
	public static boolean arePlayersInRange(Block block, double range, Player... ignored) {
		return arePlayersInRange(block.getLocation(), range, ignored);
	}

	public static void runOnAllPassengers(SignActionEvent e, Consumer<Player> f) {
		runOnAllPassengers(e.getGroup(), f);
	}

	public static void runOnAllPassengers(MinecartGroup train, Consumer<Player> f) {
		train.forEach(minecartMember -> {
			minecartMember.getEntity().getPlayerPassengers().forEach(player -> {
				f.accept(player);
			});
		});
	}

	public static String getLastStop(MinecartGroup train) {
		TrainProperties properties = train.getProperties();
		List<String> stops = new ArrayList<>(properties.getDestinationRoute());
		if (stops == null) {
			return "Unknown";
		}
		if (stops.size() < 1) {
			return "Unknown";
		}
		final int lastStopId = stops.size() - 1;
		if (lastStopId < 0) {
			return "Unknown";
		}
		return stops.get(lastStopId);
	}

	public static boolean isLastStop(MinecartGroup train, String stopName) {
		return getLastStop(train).equals(stopName);
	}

	public static boolean isNextStopLastStop(MinecartGroup train) {
		String lastStop = getLastStop(train);
		String nextStop = train.getProperties().getDestination();
		if (lastStop.equals("Unknown") || nextStop.equals("Unknown")) {
			return false;
		}
		return lastStop.equals(nextStop);
	}

	public static StopDisplayName getNextStopDisplayName(TrainCartsTransit pl, MinecartGroup train) {
		String destID = train.getProperties().getDestination();
		String stopDisplay = "Unknown";
		if (destID.isBlank()) { // if this is blank, the train may not have a route, but just a destination
			destID = train.getProperties().getDestination();
			if (destID == null) { // may still not have a destination, in which case set it to "Unknown"
				destID = "Unknown";
			} else if (destID.isBlank()) {
				destID = "Unknown";
			}
		}

		if (!destID.equalsIgnoreCase("Unknown")) {
			return pl.getStopInfoManager().getDisplayName(destID);
		} else {
			return StopDisplayName.unknownStop();
		}
	}

	public static String getNextStopSound(TrainCartsTransit pl, MinecartGroup train) {
		String destID = train.getProperties().getDestination();
		String sound = null;
		if (destID.isBlank()) { // if this is blank, the train may not have a route, but just a destination
			destID = train.getProperties().getDestination();
			if (destID == null) { // may still not have a destination, in which case return null
				return null;
			} else if (destID.isBlank()) {
				return null;
			}
		}

		if (!destID.equalsIgnoreCase("Unknown")) {
			return pl.getStopInfoManager().getSound(destID);
		} else {
			return null;
		}
	}


}
