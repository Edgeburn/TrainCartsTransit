package com.edgeburnmedia.traincartstransit.properties;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.edgeburnmedia.traincartstransit.TrainCartsTransit;

public class SignActionBellRing extends SignAction {
	private final TrainCartsTransit plugin;

	public SignActionBellRing(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean match(SignActionEvent info) {
		return info.isTrainSign() && info.getLine(1).equalsIgnoreCase("bell");
	}

	@Override
	public void execute(SignActionEvent info) {
		if (info.getAction().equals(SignActionType.GROUP_ENTER)) {
			info.getGroup().getProperties().set(plugin.getBellRungTrainProperty(), true);
		}
	}

	@Override
	public boolean build(SignChangeActionEvent event) {
		return SignBuildOptions.create().setName("Stop Request Sign").setDescription("rings the request stop bell on the train to make the train stop at the next stop")
				.handle(event.getPlayer());
	}
}
