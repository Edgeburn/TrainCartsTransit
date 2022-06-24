package com.edgeburnmedia.traincartstransit.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that implements a revolving array list. Revolving meaning that you can request the next element in the list,
 * and it will be returned. If the element is the last one in the list, the first element in the list will be returned.
 *
 * @param <T> The type of object to store in the list.
 * @author Edgeburn Media
 * @see ArrayList
 */
public class RevolvingArrayList<T> extends ArrayList<T> {
	private int currentIndex;

	public RevolvingArrayList(Collection<? extends T> c) {
		super(c);
	}

	public RevolvingArrayList() {
		super();
	}

	/**
	 * Get the next element in the list. If the element is the last one in the list, the first element in the list will
	 * be returned.
	 *
	 * @return The next element in the list.
	 */
	public T next() {
		this.currentIndex++;
		if (this.currentIndex >= size()) {
			this.currentIndex = 0;
		}
		return get(this.currentIndex);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}
}
