package com.edgeburnmedia.traincartstransit.stop;

import com.edgeburnmedia.traincartstransit.passengerinformationdisplay.NextStopInfoBar;
import com.edgeburnmedia.traincartstransit.utils.RevolvingArrayList;

import java.util.Collection;

public class StopDisplayName extends RevolvingArrayList<String> {
	private static boolean shouldTick;

	public StopDisplayName(Collection<? extends String> c) {
		super(c);
	}

	public StopDisplayName() {
		super();
	}

	public static boolean isShouldTick() {
		return shouldTick;
	}

	public static void setShouldTick(boolean shouldTick) {
		StopDisplayName.shouldTick = shouldTick;
	}

	public static StopDisplayName unknownStop() {
		StopDisplayName stopDisplayName = new StopDisplayName();
		stopDisplayName.add("Unknown");
		return stopDisplayName;
	}

	/**
	 * Set whether this is the last stop. If it is, a last stop message will be added to the rotation.
	 */
	public void setAsLastStop() {
		add(NextStopInfoBar.LAST_STOP);
	}

	@Override
	public StopDisplayName clone() {
		return (StopDisplayName) super.clone();
	}

	@Override
	public String toString() {
		return get(0); // the first element is what we want to display in cases where we can't use dynamic text
	}

	/**
	 * Using the ticker to automatically rotate the list, this method will return a ticked String to display in the
	 * {@link NextStopInfoBar}.
	 *
	 * @return The currently ticked element.
	 */
	public String getTicked() { // TODO: this doesn't work
		if (isShouldTick()) {
			setShouldTick(false);
			return next();
		} else {
			return get(getCurrentIndex());
		}
	}
}
