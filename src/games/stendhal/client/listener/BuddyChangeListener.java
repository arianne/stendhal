/*
 * @(#) src/games/stendhal/client/events/BuddyChangeListener.java
 *
 * $Id$
 */

package games.stendhal.client.listener;

/**
 * A listener of buddy events.
 */
public interface BuddyChangeListener {
	/**
	 * A buddy was added.
	 * 
	 * @param buddyName
	 *            The name of the buddy.
	 */
	void buddyAdded(String buddyName);

	/**
	 * A buddy went offline.
	 * 
	 * @param buddyName
	 *            The name of the buddy.
	 */
	void buddyOffline(String buddyName);

	/**
	 * A buddy went online.
	 * 
	 * @param buddyName
	 *            The name of the buddy.
	 */
	void buddyOnline(String buddyName);

	/**
	 * A buddy was removed.
	 * 
	 * @param buddyName
	 *            The name of the buddy.
	 */
	void buddyRemoved(String buddyName);
}
