package com.edgeburnmedia.traincartstransit.signaction;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.edgeburnmedia.traincartstransit.TrainCartsTransit;

import static com.edgeburnmedia.traincartstransit.utils.TrtUtils.announceNextStop;

public class SignActionAnnouncement extends SignAction {
	private final TrainCartsTransit plugin;

	public SignActionAnnouncement(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean match(SignActionEvent info) {
		return info.isType("announcement") && info.isTrainSign();
	}

	@Override
	public void execute(SignActionEvent info) {
		if (info.getAction().equals(SignActionType.GROUP_LEAVE)) {
			announceNextStop(plugin, info.getGroup());
		}
	}

	@Override
	public boolean build(SignChangeActionEvent event) {
		return SignBuildOptions.create().setName("Announcement Sign").setDescription("announce the train's next stop")
				.handle(event.getPlayer());
	}
}
