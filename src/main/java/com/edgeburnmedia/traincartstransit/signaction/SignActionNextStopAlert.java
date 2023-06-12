package com.edgeburnmedia.traincartstransit.signaction;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.edgeburnmedia.traincartstransit.TrainCartsTransit;

import static com.edgeburnmedia.traincartstransit.utils.TrtUtils.announceNextStop;

public class SignActionNextStopAlert extends SignAction {
	private final TrainCartsTransit plugin;

	public SignActionNextStopAlert(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean match(SignActionEvent info) {
		return info.isType("nextstopalert") && info.isTrainSign();
	}

	@Override
	public void execute(SignActionEvent info) {
		if (info.getAction().equals(SignActionType.GROUP_LEAVE)) {
			announceNextStop(plugin, info.getGroup());
		}
	}

	@Override
	public boolean build(SignChangeActionEvent event) {
		return SignBuildOptions.create().setName("Next Stop Alert Sign").setDescription("alert passenger's of the train's next stop")
				.handle(event.getPlayer());
	}
}
