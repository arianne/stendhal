/*
 * @(#) src/games/stendhal/client/events/RPObjectChangeListener.java
 *
 * $Id$
 */

package games.stendhal.client.events;

//
//

import marauroa.common.game.RPObject;

/**
 * A listener of RPObject changes.
 */
public interface RPObjectChangeListener {
	/**
	 * An object was added.
	 *
	 * @param	object		The object.
	 */
	public void onAdded(RPObject object);

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	public void onChangedAdded(RPObject object, RPObject changes);

	/**
	 * A slot object added/changed attribute(s).
	 *
	 * @param	container	The base container object.
	 * @param	slotName	The container's slot name.
	 * @param	object		The base slot object.
	 * @param	changes		The slot changes.
	 */
	public void onChangedAdded(RPObject container, String slotName, RPObject object, RPObject changes);

	/**
	 * The object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	public void onChangedRemoved(RPObject object, RPObject changes);

	/**
	 * A slot object removed attribute(s).
	 *
	 * @param	container	The base container object.
	 * @param	slotName	The container's slot name.
	 * @param	object		The base slot object.
	 * @param	changes		The slot changes.
	 */
	public void onChangedRemoved(RPObject container, String slotName, RPObject object, RPObject changes);

	/**
	 * An object was removed.
	 *
	 * @param	object		The object.
	 */
	public void onRemoved(RPObject object);
}
