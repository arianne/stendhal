/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

/**
 * This Singleton should contain all NPCs in the Stendhal world that are unique.
 */
public class NPCList implements Iterable<SpeakerNPC> {

	private static Logger logger = Logger.getLogger(NPCList.class);

	/** The singleton instance. */
	private static NPCList instance;

	private final Map<String, SpeakerNPC> contents;

	/** Names reserved for NPCs created dynamically. */
	private final Set<String> reserved = Sets.newHashSet("patrick"); // Herald NPC (games.stendhal.server.script.Herald)


	/**
	 * Returns the Singleton instance.
	 *
	 * @return The instance
	 */
	public static NPCList get() {
		if (instance == null) {
			instance = new NPCList();
		}

		return instance;
	}

	protected NPCList() {
		contents = new HashMap<String, SpeakerNPC>();

	}

	/**
	 * Returns the NPC with the given name.
	 *
	 * @param name
	 *            The NPC's name
	 * @return The NPC, or null if there is no NPC with this name
	 */
	public SpeakerNPC get(final String name) {
		return contents.get(name.toLowerCase(Locale.ENGLISH));
	}

	/**
	 * Checks whether an NPC with the given name exists.
	 *
	 * @param name
	 *            The NPC's name
	 * @return true iff an NPC with the given name exists
	 */
	public boolean has(final String name) {
		return contents.containsKey(name.toLowerCase(Locale.ENGLISH));
	}

	/**
	 * Adds an NPC to the NPCList. Does nothing if an NPC with the same name
	 * already exists. This makes sure that each NPC can be uniquely identified
	 * by his/her name.
	 *
	 * @param npc
	 *            The NPC that should be added
	 */
	public void add(final SpeakerNPC npc) {
		// insert lower case names to allow case insensitive
		// searches for teleport commands, etc.
		final String name = npc.getName().toLowerCase(Locale.ENGLISH);

		if (contents.containsKey(name)) {
			logger.error("Not adding " + npc
					+ " to NPCList because there is already an NPC called "
					+ npc.getName());
		} else if (reserved.contains(name)) {
			logger.error("Not adding " + npc
					+ " to NPCList because name "
					+ npc.getName() + " is reserved");
		} else {
			contents.put(name, npc);
		}
	}

	/**
	 * Removes an NPC from the NPCList. Does nothing if no NPC with the given
	 * name exists.
	 *
	 * @param name
	 *            The name of the NPC that should be removed
	 * @return SpeakerNPC or null in case it was not in the list
	 */
	public SpeakerNPC remove(final String name) {
		return contents.remove(name.toLowerCase(Locale.ENGLISH));
	}

	/**
	 * Returns a list of all NPCs.
	 *
	 * @return list of npcs
	 */
	public Set<String> getNPCs() {
		// do not expose the internal structure but return a copy instead
		return new TreeSet<String>(contents.keySet());
	}

	/**
	 * Removes all NPCs from this list.
	 */
	public void clear() {
		contents.clear();
	}

	/**
	 * @return  the interator over the SpeakerNPC objects.
	 */
	@Override
	public Iterator<SpeakerNPC> iterator() {
		return contents.values().iterator();
	}

	/**
	 * Call when dynamically created NPC with reserved name is removed from world.
	 */
	public void reserve(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		reserved.add(name);
	}

	/**
	 * Call when an NPC with reserved name is created dynamically.
	 */
	public void unreserve(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		reserved.remove(name);
	}
}
