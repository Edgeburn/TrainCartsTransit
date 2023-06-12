package com.edgeburnmedia.traincartstransit.passengerinformationdisplay;

import com.edgeburnmedia.traincartstransit.TrainCartsTransit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Manages the playing of the transit tones. An instance of this class should be retrieved from the main plugin class.
 *
 * @author Edgeburn Media
 */
public class TransitTonesPlayer {
	private TrainCartsTransit pl;

	public TransitTonesPlayer(TrainCartsTransit pl) {
		this.pl = pl;
	}

	/**
	 * Plays the request stop tone for the player. This is used when the player requests to stop at a stop.
	 * Note that this method does not actually set the flag for the player to stop at a stop, it just plays the tone.
	 *
	 * @param player The player to play the sound for.
	 */
	public static void playBellRungTone(Player player) {
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, Float.MAX_VALUE, (float) Math.pow(2, -1.0 / 12.0));
	}

	/**
	 * Plays the next stop tone for the player, and then plays the sound for the announcement of the next stop.
	 *
	 * @param player    The player to play the sound for.
	 * @param stopSound The sound to play for the announcement of the next stop.
	 */
	public void playNextStopTone(Player player, String stopSound) {

		final float volume = 99999f;

		new BukkitRunnable() {
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
				// using 99999 here so that when the sound is played as the train is moving, it doesn't
				// become impossible to hear
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, volume, 0.0f);
				new BukkitRunnable() {

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
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, volume, (float) Math.pow(2, -7.0 / 12.0));
						if (stopSound != null) {
							new BukkitRunnable() {
								@Override
								public void run() {
									player.playSound(player.getLocation(), stopSound, volume, 1.0f);
								}
							}.runTaskLater(pl, 6);
						}
					}
				}.runTaskLater(pl, 4);
			}
		}.runTask(pl);
	}
}
