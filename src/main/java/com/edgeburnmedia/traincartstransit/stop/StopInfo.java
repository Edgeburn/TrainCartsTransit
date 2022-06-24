package com.edgeburnmedia.traincartstransit.stop;

import java.util.List;

public class StopInfo {
	/**
	 * The TrainCarts "destination" property
	 */
	private String destinationName;
	private String soundId;
	private StopDisplayName nextStopDisplayName;

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getSoundId() {
		return soundId;
	}

	public void setSoundId(String soundId) {
		this.soundId = soundId;
	}

	public StopDisplayName getNextStopDisplayName() {
		return nextStopDisplayName;
	}

	/**
	 * Set the next stop display name. This is the text that will be displayed in the next stop info bar.
	 *
	 * @param nextStopDisplayName The list of Strings to display.
	 */
	public void setNextStopDisplayName(List<String> nextStopDisplayName) {
		StopDisplayName stopDisplayName = new StopDisplayName();
		stopDisplayName.addAll(nextStopDisplayName);
		setNextStopDisplayName(stopDisplayName);
	}

	public void setNextStopDisplayName(StopDisplayName nextStopDisplayName) {
		this.nextStopDisplayName = nextStopDisplayName;
	}
}
