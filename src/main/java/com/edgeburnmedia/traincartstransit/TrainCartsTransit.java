package com.edgeburnmedia.traincartstransit;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.edgeburnmedia.traincartstransit.managers.StopInfoManager;
import com.edgeburnmedia.traincartstransit.managers.TrainRouteAnnouncementManager;
import com.edgeburnmedia.traincartstransit.passengerinformationdisplay.NextStopInfoBar;
import com.edgeburnmedia.traincartstransit.passengerinformationdisplay.TransitTonesPlayer;
import com.edgeburnmedia.traincartstransit.properties.BellRung;
import com.edgeburnmedia.traincartstransit.properties.RouteID;
import com.edgeburnmedia.traincartstransit.stop.SignActionStop;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public final class TrainCartsTransit extends JavaPlugin {
	private final SignActionStop signActionStop = new SignActionStop(this);
	private final TransitTonesPlayer transitTonesPlayer = new TransitTonesPlayer(this);
	private final TrainRouteAnnouncementManager announcementManager = new TrainRouteAnnouncementManager(this);
	private RouteID routeID;
	private BellRung bellRungTrainProperty;
	private NextStopInfoBar infoBar = new NextStopInfoBar(this);

	private StopInfoManager stopInfoManager = new StopInfoManager(this);

	public StopInfoManager getStopInfoManager() {
		return stopInfoManager;
	}

	public BellRung getBellRungTrainProperty() {
		return bellRungTrainProperty;
	}

	public TrainRouteAnnouncementManager getAnnouncementManager() {
		return announcementManager;
	}

	public TransitTonesPlayer getTransitTonesPlayer() {
		return transitTonesPlayer;
	}

	@Override
	public void onDisable() {
		SignAction.unregister(signActionStop);
		getLogger().info("Unregistered SignActionStop");
	}

	@Override
	public void onEnable() {
		TrtListeners listeners = new TrtListeners(this);
		getServer().getPluginManager().registerEvents(listeners, this);
		SignAction.register(signActionStop);
		routeID = new RouteID(this);
		bellRungTrainProperty = new BellRung();
		getPlugin(TrainCarts.class).getPropertyRegistry().register(routeID);
		getPlugin(TrainCarts.class).getPropertyRegistry().register(bellRungTrainProperty);

		getServer().getPluginCommand("trtdebug").setExecutor(new DebugCommands(this));
		getServer().getPluginCommand("traincartstransit").setExecutor(routeID);

		announcementManager.saveConfigFile();
		announcementManager.readConfigFile();

		stopInfoManager.saveConfigFile();
		stopInfoManager.readConfigFile();

		infoBar.runTaskTimer(this, 1, 1);
	}

	public RouteID getRouteID() {
		return routeID;
	}

	private void setUpCloudCommands() throws Exception {
		CommandManager<CommandSender> commandManager;
		AnnotationParser<CommandSender> parser;
		commandManager = new BukkitCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
		parser = new AnnotationParser<>(commandManager, CommandSender.class, parserParameters -> SimpleCommandMeta.empty());
		parser.parse(routeID).forEach(commandSenderCommand -> {
			commandManager.getCommandRegistrationHandler().registerCommand(commandSenderCommand);
		});
	}
}
