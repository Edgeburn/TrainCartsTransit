package com.edgeburnmedia.traincartstransit.signaction;

import com.bergerkiller.bukkit.tc.Station;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import com.edgeburnmedia.traincartstransit.stop.Stop;
import com.edgeburnmedia.traincartstransit.utils.TrtUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SignActionStop extends SignAction {
	private final TrainCartsTransit plugin;

	public SignActionStop(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}

	/**
	 * Checks whether a sign action event is meant for this type of Sign Action
	 *
	 * @param info event
	 * @return True if it matched, False if not
	 */
	@Override
	public boolean match(SignActionEvent info) {
		return info.isType("stop") && info.isTrainSign();
	}

	/**
	 * Fired when this sign is being executed for a certain event
	 *
	 * @param info event
	 */
	@Override
	public void execute(SignActionEvent info) {
		/*
		 * On a train entering the station, the train will stop if there is a player nearby, or if the train has the
		 * bell rung flag set by a passenger left-clicking the air. It would also reset the bell flag of the train.
		 * It would also play the announcement sound of the train's route.
		 *
		 * On a train leaving the station, the train will then play the next station's announcement sound.
		 */
		MinecartGroup train = info.getGroup();

		// on train entering...
		if (info.getAction().equals(SignActionType.GROUP_ENTER)) {
			Station.StationConfig stationConfig = Station.StationConfig.fromSign(info);
			Stop stop = new Stop(info, stationConfig);
			if (shouldStop(info)) {
				stop.centerTrain();
				playRouteAnnouncementSound(info);
				stop.waitTrain(stop.getDelay());
				stop.launchTo(stop.getNextDirectionFace());
			} else {
				stop.launchTo(stop.getNextDirectionFace()); /* we still want to launch the train, so it doesn't lose
				its momentum */
			}
		} else if (info.getAction().equals(SignActionType.GROUP_LEAVE)) {
			TrtUtils.announceNextStop(plugin, train);
		}
	}

	/**
	 * Fired when a sign is being built
	 *
	 * @param event containing relevant Build information
	 * @return True if building is allowed, False if not
	 */
	@Override
	public boolean build(SignChangeActionEvent event) {
		return SignBuildOptions.create().setName("Smart Stop Sign").setDescription("Like a Station, but only stops " +
						"trains when there is a player nearby, or train passengers ring the stop bell by left clicking the air")
				.handle(event.getPlayer());
	}

	public void playRouteAnnouncementSound(SignActionEvent e) {
		String routeID = e.getGroup().getProperties().get(plugin.getRouteID());
		String sound = plugin.getAnnouncementManager().getSound(routeID);
		e.getLocation().getWorld().playSound(e.getLocation(), sound, 1.0f, 1.0f);
	}

	private boolean shouldStop(SignActionEvent e) {
		List<Player> nearbyPlayers = new ArrayList<>();
		List<Player> trainPassengers = new ArrayList<>();
		TrtUtils.runOnAllPassengers(e, player -> {
			trainPassengers.add(player);
		});

		Player[] passengers = trainPassengers.toArray(new Player[0]);
		boolean playersInRange = TrtUtils.arePlayersInRange(e.getLocation(), 5.0, passengers);
		boolean bellRung = e.getGroup().getProperties().get(plugin.getBellRungTrainProperty());
		e.getGroup().getProperties().set(plugin.getBellRungTrainProperty(), false);

		return playersInRange || bellRung;
	}
}
