package com.edgeburnmedia.traincartstransit.stop;

import com.edgeburnmedia.traincartstransit.passengerinformationdisplay.NextStopInfoBar;
import com.edgeburnmedia.traincartstransit.utils.RevolvingArrayList;

import java.util.Collection;
import java.util.Collections;

public class StopDisplayName extends RevolvingArrayList<String> {

	public StopDisplayName(Collection<? extends String> c) {
		super(c);
	}

	public StopDisplayName() {
		super();
	}

	public StopDisplayName(String destination) {
		this(Collections.singletonList(destination));
	}

	private static boolean shouldTickDisplay() {
		long m = System.currentTimeMillis();
		long s = m / 1000;
		if (s % 2 == 0) {
			return true;
		} else {
			return false;
		}
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
	public String toString() {
		return get(0); // the first element is what we want to display in cases where we can't use dynamic text
	}

	/**
	 * Using the ticker to automatically rotate the list, this method will return a ticked String to display in the
	 * {@link NextStopInfoBar}.
	 *
	 * @return The currently ticked element.
	 */
	public String getTicked() {
		if (shouldTickDisplay()) {
			return next();
		} else {
			return get(getCurrentIndex());
		}
	}
}
