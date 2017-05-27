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
package games.stendhal.client.gui.buddies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import org.apache.log4j.Logger;

/**
 * A <code>ListModel</code> for buddies that keeps itself sorted first by online
 * status and secondarily by buddy name.
 */
class BuddyListModel extends AbstractListModel<Buddy> {
	/*
	 * LinkedHashMap would nicely combine order and fast searches, but
	 * unfortunately it does not allow sorting after creation. (Likewise for
	 * TreeMap).
	 *
	 * The map is for a quick lookup, the list is needed for the ordering.
	 */
	private final List<Buddy> buddyList = new ArrayList<Buddy>();
	private final Map<String, Buddy> buddyMap = new HashMap<String, Buddy>();

	@Override
	public Buddy getElementAt(int index) {
		return buddyList.get(index);
	}

	@Override
	public int getSize() {
		return buddyList.size();
	}

	/**
	 * Set the online status of a buddy. Add a new buddy if one by the wanted
	 * name does not already exist.
	 *
	 * @param name name of the buddy
	 * @param online <code>true</code> if the buddy is at the moment online,
	 * 	false otherwise
	 */
	void setOnline(String name, boolean online) {
		if (name == null) {
			Logger.getLogger(BuddyListModel.class).error("Buddy with no name set " + (online ? "online" : "offline"));
			return;
		}
		Buddy buddy = buddyMap.get(name);
		if (buddy == null) {
			buddy = new Buddy(name);
			buddy.setOnline(online);
			buddyList.add(buddy);
			buddyMap.put(name, buddy);
			Collections.sort(buddyList);
			int index = buddyList.indexOf(buddy);
			fireIntervalAdded(this, index, index);
		} else {
			int index1 = buddyList.indexOf(buddy);
			boolean changed = buddy.setOnline(online);
			Collections.sort(buddyList);
			int index2 = buddyList.indexOf(buddy);
			if (changed) {
				fireContentsChanged(this, index1, index2);
			}
		}
	}

	/**
	 * Remove a buddy from the list.
	 *
	 * @param name name of the removed player
	 */
	void removeBuddy(String name) {
		Buddy buddy = buddyMap.get(name);
		if (buddy != null) {
			buddyMap.remove(name);
			int index = buddyList.indexOf(buddy);
			buddyList.remove(buddy);
			fireIntervalRemoved(this, index, index);
		}
	}
}
