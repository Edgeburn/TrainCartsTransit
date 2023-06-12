package com.edgeburnmedia.traincartstransit;

import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class DebugCommands implements CommandExecutor, TabCompleter {
	private final TrainCartsTransit plugin;

	public DebugCommands(TrainCartsTransit plugin) {
		this.plugin = plugin;
	}

	/**
	 * Executes the given command, returning its success.
	 * <br>
	 * If false is returned, then the "usage" plugin.yml entry for this command (if defined) will be sent to the
	 * player.
	 *
	 * @param sender  Source of the command
	 * @param command Command which was executed
	 * @param label   Alias of the command which was used
	 * @param args    Passed command arguments
	 * @return true if a valid command, otherwise false
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args == null) {
			return false;
		}
		switch (args[0]) {
			case "next-stop-tone":
				if (args.length < 2) {
					plugin.getTransitTonesPlayer().playNextStopTone((Player) sender, null);
				} else {
					plugin.getTransitTonesPlayer().playNextStopTone((Player) sender, args[1]);
				}
				break;
			case "stop-request-tone":
				plugin.getTransitTonesPlayer().playBellRungTone((Player) sender);
				break;
			case "help":
				sender.sendMessage("/trtdebug next-stop-tone [stop] - play the next stop tone for the given stop");
				sender.sendMessage("/trtdebug stop-request-tone - play the stop request tone");
				break;
			case "list-nodes":
				Collection<PathNode> nodes = plugin.getTrainCartsPlugin().getPathProvider().getWorld("world").getNodes();
				for (int i = 0; i < nodes.size(); i++) {
					final PathNode node = (PathNode) nodes.toArray()[i];
					final Collection<String> names = node.getNames();
					for (int j = 0; j < names.size(); j++) {
						sender.sendMessage("Node " + i + " name " + j + " ( " + node.location + ") : " + names.toArray()[j]);
					}
				}

		}

		return true;
	}

	/**
	 * Requests a list of possible completions for a command argument.
	 *
	 * @param sender  Source of the command.  For players tab-completing a command inside of a command block, this will
	 *                be the player, not the command block.
	 * @param command Command which was executed
	 * @param label   Alias of the command which was used
	 * @param args    The arguments passed to the command, including final partial argument to be completed
	 * @return A List of possible completions for the final argument, or null to default to the command executor
	 */
	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		return List.of("next-stop-tone", "stop-request-tone", "help", "list-nodes");
	}
}
