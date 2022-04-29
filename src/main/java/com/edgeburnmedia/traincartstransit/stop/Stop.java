package com.edgeburnmedia.traincartstransit.stop;

import com.bergerkiller.bukkit.tc.Station;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import org.bukkit.Location;

/**
 * A stop on a train route. Contains a stop id, display name, {@link org.bukkit.Location}, ...
 *
 * @author Edgeburn Media
 */
public class Stop extends Station {
	private int id;
	private Location stopLocation;

	/**
	 * Initializes a Station for a sign, reading station configuration
	 * from the sign using station sign syntax.
	 *
	 * @param info Station sign action event
	 */
	public Stop(SignActionEvent info) {
		super(info);
	}

	/**
	 * Initializes a Station for a sign. The station configuration
	 * can be specified and is not read from the sign text.
	 *
	 * @param info   Sign that acts as a station
	 * @param config Station configuration
	 */
	public Stop(SignActionEvent info, StationConfig config) {
		super(info, config);
	}


	// TODO: write this class
}
