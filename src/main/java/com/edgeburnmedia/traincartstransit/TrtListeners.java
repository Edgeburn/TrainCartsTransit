package com.edgeburnmedia.traincartstransit;

import com.edgeburnmedia.traincartstransit.utils.TrtUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TrtListeners implements Listener {
	private final TrainCartsTransit plugin;

	public TrtListeners(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onRightClickInAir(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_AIR)) && player.getVehicle() != null) {
			TrtUtils.ringBellOfPlayerTrain(plugin, player);
		}
	}
}
