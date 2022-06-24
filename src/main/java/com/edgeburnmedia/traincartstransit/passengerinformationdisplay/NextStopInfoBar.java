package com.edgeburnmedia.traincartstransit.passengerinformationdisplay;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import com.edgeburnmedia.traincartstransit.stop.StopDisplayName;
import com.edgeburnmedia.traincartstransit.utils.TrtUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class NextStopInfoBar extends BukkitRunnable {
	public static final String LAST_STOP = "" + ChatColor.RED + ChatColor.BOLD + "LAST STOP";
	private static final String TICK_MARK = "✓";
	private static boolean shouldTickDisplay;
	private final TrainCartsTransit pl;

	public NextStopInfoBar(TrainCartsTransit pl) {
		this.pl = pl;
	}

	public static boolean isShouldTickDisplay() {
		return shouldTickDisplay;
	}

	public static void setShouldTickDisplay(boolean shouldTickDisplay) {
		NextStopInfoBar.shouldTickDisplay = shouldTickDisplay;
	}

	/**
	 * Returns the check mark symbol if the train is stopping at the next stop.
	 * Otherwise, returns an empty string.
	 *
	 * @param train the train to check
	 * @return the check mark symbol if the train is stopping at the next stop.
	 * Otherwise, returns an empty string.
	 */
	private String checkMarkSymbolIfStopping(MinecartGroup train) {
		boolean bellRung = train.getProperties().get(pl.getBellRungTrainProperty());

		if (bellRung) {
			return " " + ChatColor.GOLD + ChatColor.BOLD + TICK_MARK;
		} else {
			return "";
		}
	}

	/**
	 * When an object implementing interface {@code Runnable} is used
	 * to create a thread, starting the thread causes the object's
	 * {@code run} method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method {@code run} is that it may
	 * take any action whatsoever.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		pl.getServer().getOnlinePlayers().forEach(player -> {
			Entity playerVehicle = player.getVehicle();
			MinecartMember<?> member = MinecartMemberStore.getFromEntity(playerVehicle);
			if (member != null) {
				MinecartGroup train = member.getGroup();
				if (train != null) {
					StopDisplayName stopDisplayName = new StopDisplayName(TrtUtils.getNextStopDisplayName(pl, train));
					if (TrtUtils.isNextStopLastStop(train)) {
						stopDisplayName.setAsLastStop();
					}
					String currentDisplay = stopDisplayName.getTicked();
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							TextComponent.fromLegacyText(
									ChatColor.GOLD + currentDisplay + ChatColor.RESET
											+ checkMarkSymbolIfStopping(train)));
				}
			}
		});
	}
}
