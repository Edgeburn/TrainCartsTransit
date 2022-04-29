package com.edgeburnmedia.traincartstransit.stop;

public class StopInfo {
	/**
	 * The TrainCarts "destination" property
	 */
	private String destinationName;
	private String soundId;
	private String nextStopDisplayName;

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

	public String getNextStopDisplayName() {
		return nextStopDisplayName;
	}

	public void setNextStopDisplayName(String nextStopDisplayName) {
		this.nextStopDisplayName = nextStopDisplayName;
	}
}
