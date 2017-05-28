/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;

import org.apache.log4j.Logger;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SlotOwner;


/**
 * utility methods for working with keyed slots
 *
 * @author hendrik
 */
public class KeyedSlotUtil {
	private static Logger logger = Logger.getLogger(KeyedSlotUtil.class);

	/**
	 * Returns the single object of a "keyed slot".
	 *
	 * @param slotOwner the object owning the slot
	 * @param name name of key slot
	 * @return object or <code>null</code> it does not exist
	 */
	public static RPObject getKeyedSlotObject(final SlotOwner slotOwner, final String name) {
		if (!slotOwner.hasSlot(name)) {
			logger.error("Expected to find " + name + " slot in " + slotOwner, new Throwable());
			return null;
		}

		final RPSlot slot = slotOwner.getSlot(name);

		if (slot.size() == 0) {
			logger.error("Found empty " + name + " slot" + slotOwner, new Throwable());
			return null;
		}

		return slot.iterator().next();
	}

	/**
	 * Get a keyed string value on a named slot.
	 *
	 * @param slotOwner the object owning the slot
	 * @param name
	 *            The slot name.
	 * @param key
	 *            The value key.
	 *
	 * @return The keyed value of the slot, or <code>null</code> if not set.
	 */
	public static String getKeyedSlot(final SlotOwner slotOwner, final String name, final String key) {
		final RPObject object = KeyedSlotUtil.getKeyedSlotObject(slotOwner, name);
		if (object == null) {
			return null;
		}

		return object.get(key);
	}

	/**
	 * Set a keyed string value on a named slot.
	 *
	 * @param slotOwner the object owning the slot
	 * @param name
	 *            The slot name.
	 * @param key
	 *            The value key.
	 * @param value
	 *            The value to assign (or remove if <code>null</code>).
	 *
	 * @return <code>true</code> if value changed, <code>false</code> if
	 *         there was a problem.
	 */
	public static boolean setKeyedSlot(final SlotOwner slotOwner, final String name, final String key, final String value) {
		final RPObject object = KeyedSlotUtil.getKeyedSlotObject(slotOwner, name);
		if (object == null) {
			return false;
		}

		if (value != null) {
			object.put(key, value);
		} else if (object.has(key)) {
			object.remove(key);
		}

		return true;
	}
}
