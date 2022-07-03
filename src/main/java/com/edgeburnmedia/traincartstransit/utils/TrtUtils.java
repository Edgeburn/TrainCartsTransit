package com.edgeburnmedia.traincartstransit.utils;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import com.edgeburnmedia.traincartstransit.passengerinformationdisplay.TransitTonesPlayer;
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

/**
 * Utility methods for Train Carts Transit.
 *
 * @author Edgebun Media
 */
public final class TrtUtils {

	/**
	 * Ring the bell of a train.
	 *
	 * @param plugin Plugin instance
	 * @param player Ring the bell of this player's train.
	 */
	public static void ringBellOfPlayerTrain(TrainCartsTransit plugin, Player player) {
		Entity vehicle = player.getVehicle();
		MinecartMember<?> member = MinecartMemberStore.getFromEntity(vehicle);
		MinecartGroup train = member.getGroup();
		train.getProperties().set(plugin.getBellRungTrainProperty(), true);
		TransitTonesPlayer.playBellRungTone(player);
		displayStopRequestTitle(plugin, player);
	}

	/**
	 * Displays a title to the player indicating that they have requested a stop.
	 *
	 * @param plugin Plugin instance
	 * @param player Player who rung the bell
	 */
	public static void displayStopRequestTitle(TrainCartsTransit plugin, Player player) {
		Entity vehicle = player.getVehicle();
		MinecartMember<?> member = MinecartMemberStore.getFromEntity(vehicle);
		MinecartGroup train = member.getGroup();
		StopDisplayName nextStopDisplayName = TrtUtils.getNextStopDisplayName(plugin, train);
		TrtUtils.runOnAllPassengers(train, p -> {
			p.sendTitle(ChatColor.GOLD + "STOP REQUESTED", ChatColor.GOLD + "Stopping at " + nextStopDisplayName, 20, 70, 20);
		});
	}

	/**
	 * Display a message to all passengers of a train indicating that the next stop is the last stop.
	 *
	 * @param plugin Plugin instance
	 * @param train  Train whose passengers should be notified
	 */
	public static void displayLastStopTitle(TrainCartsTransit plugin, MinecartGroup train) {
		StopDisplayName nextStopDisplayName = TrtUtils.getNextStopDisplayName(plugin, train);
		TrtUtils.runOnAllPassengers(train, player -> {
			player.sendTitle(ChatColor.RED + "LAST STOP", ChatColor.GOLD + "The last stop is " + nextStopDisplayName, 20, 70, 20);
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

	/**
	 * Convenience method for running a {@link Consumer} on all passengers of a {@link MinecartGroup} directly
	 * from a {@link SignActionEvent}.
	 *
	 * @param e {@link SignActionEvent} to get the {@link MinecartGroup} from
	 * @param f {@link Consumer} to run on all passengers
	 */
	public static void runOnAllPassengers(SignActionEvent e, Consumer<Player> f) {
		runOnAllPassengers(e.getGroup(), f);
	}

	/**
	 * Runs a function on all passengers of a {@link MinecartGroup}
	 *
	 * @param train {@link MinecartGroup} to run the function on
	 * @param f     Function to run
	 */
	public static void runOnAllPassengers(MinecartGroup train, Consumer<Player> f) {
		train.forEach(minecartMember -> {
			minecartMember.getEntity().getPlayerPassengers().forEach(player -> {
				f.accept(player);
			});
		});
	}

	/**
	 * Get the destination name of the last stop of a train.
	 *
	 * @param train The train to get the destination name of.
	 * @return The destination name of the last stop of the train.
	 */
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

	/**
	 * Determine whether a given stop is the last stop for a train.
	 *
	 * @param train    The train to check.
	 * @param stopName The stop to check.
	 * @return True if the stop is the last stop, False if not.
	 */
	public static boolean isLastStop(MinecartGroup train, String stopName) {
		return getLastStop(train).equals(stopName);
	}

	/**
	 * Determine whether the train's next stop is last stop.
	 *
	 * @param train The train to check.
	 * @return True if the train's next stop is the last stop, False if not.
	 */
	public static boolean isNextStopLastStop(MinecartGroup train) {
		String lastStop = getLastStop(train);
		String nextStop = train.getProperties().getDestination();
		if (lastStop.equals("Unknown") || nextStop.equals("Unknown")) {
			return false;
		}
		return lastStop.equals(nextStop);
	}

	/**
	 * Gets the {@link StopDisplayName} of the next stop on a train's route.
	 *
	 * @param pl    Plugin instance
	 * @param train Train to get the next stop of
	 * @return The next stop's {@link StopDisplayName}
	 */
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

	/**
	 * Gets the sound name to play for the next stop on a train's route
	 *
	 * @param pl    Plugin instance
	 * @param train Train to get the next stop for
	 * @return Sound name to play
	 */
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


	/**
	 * Announce the next stop of the train to all players riding the train
	 *
	 * @param plugin Plugin instance
	 * @param train  Train to announce
	 */
	public static void announceNextStop(TrainCartsTransit plugin, MinecartGroup train) {
		String sound = getNextStopSound(plugin, train);
		runOnAllPassengers(train, player -> {
			plugin.getTransitTonesPlayer().playNextStopTone(player, sound);
			if (isNextStopLastStop(train)) {
				displayLastStopTitle(plugin, train);
			}
		});
	}
}
