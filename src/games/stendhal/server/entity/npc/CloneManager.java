/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;


/**
 * Manages registered SpeakerNPC clones.
 */
public class CloneManager {

	private static CloneManager instance;

	private final static Map<String, List<String>> cloneList = new HashMap<>();

	public static CloneManager getInstance() {
		if (instance == null) {
			instance = new CloneManager();
		}

		return instance;
	}

	/**
	 * Creates a new clone.
	 *
	 * @param orig
	 * 		The SpeakerNPC to be cloned.
	 * @param cloneName
	 * 		The hidden actual name of the entity. If <code>null</code>, the name
	 * 		of the original NPC suffixed with an integer index will be used.
	 * @return
	 * 		New SpeakerNPC.
	 */
	public SpeakerNPC clone(final SpeakerNPC orig, String cloneName) {
		SpeakerNPC clone = null;

		if (orig != null) {
			final String origName = orig.getName();
			List<String> registered = cloneList.get(origName);

			int idx;
			if (registered == null) {
				idx = 2;
				registered = new ArrayList<String>();
				cloneList.put(origName, registered);
			} else {
				idx = registered.size() + 2;
			}

			if (cloneName == null) {
				cloneName = origName + Integer.toString(idx);
			}
			registered.add(cloneName);

			clone = new SpeakerNPC(cloneName);
			clone.put("cloned", origName);

			final List<String> copyAttributes = Arrays.asList(
					"outfit_ext", "description"
			);
			final Map<String, String> alternateAttributes = new HashMap<String, String>() {{
				put("outfit_ext", "class");
			}};

			for (String attribute: copyAttributes) {
				if (orig.has(attribute)) {
					clone.put(attribute, orig.get(attribute));
				} else if (alternateAttributes.containsKey(attribute)) {
					attribute = alternateAttributes.get(attribute);
					if (orig.has(attribute)) {
						clone.put(attribute, orig.get(attribute));
					}
				}
			}

			// clones should not be displayed on website, but check for alternative image just to be safe
			clone.setAlternativeImage(orig.getAlternativeImage());
		}

		return clone;
	}

	/**
	 * Creates a new clone.
	 *
	 * @param orig
	 * 		The SpeakerNPC to be cloned.
	 * @return
	 * 		New SpeakerNPC.
	 */
	public SpeakerNPC clone(final SpeakerNPC orig) {
		return clone(orig, null);
	}

	/**
	 * Creates a new clone.
	 *
	 * @param name
	 * 		Name of the SpeakerNPC to be cloned.
	 * @param cloneName
	 * 		The hidden actual name of the entity. If <code>null</code>, the name
	 * 		of the original NPC suffixed with an integer index will be used.
	 * @return
	 * 		New SpeakerNPC.
	 */
	public SpeakerNPC clone(final String name, final String cloneName) {
		return clone(SingletonRepository.getNPCList().get(name), cloneName);
	}

	/**
	 * Creates a new clone.
	 *
	 * @param name
	 * 		Name of the SpeakerNPC to be cloned.
	 * @return
	 * 		New SpeakerNPC.
	 */
	public SpeakerNPC clone(final String name) {
		return clone(name, null);
	}

	/**
	 * Checks if a name is registered as a clone.
	 *
	 * @param name
	 * 		Name to be checked.
	 * @return
	 * 		<code>true</code> if the name is found in the registered list.
	 */
	public boolean isClone(final String name) {
		for (final List<String> clones: cloneList.values()) {
			if (clones.contains(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if a SpeakerNPC is registered as a clone.
	 *
	 * @param npc
	 * 		The SpeakerNPC to be checked.
	 * @return
	 * 		<code>true</code> if the name of the SpeakerNPC is found in the registered list.
	 */
	public boolean isClone(final SpeakerNPC npc) {
		return isClone(npc.getName());
	}

	/**
	 * Retrieves the original SpeakerNPC.
	 *
	 * @param name
	 * 		Name of the clone.
	 * @return
	 * 		The original SpeakerNPC if the clone name was found in the registered list,
	 * 		otherwise <code>null</code>.
	 */
	public SpeakerNPC getOriginal(final String name) {
		SpeakerNPC orig = null;

		for (final String key: cloneList.keySet()) {
			if (cloneList.get(key).contains(name)) {
				orig = SingletonRepository.getNPCList().get(key);
				break;
			}
		}

		return orig;
	}

	/**
	 * Retrieves the original SpeakerNPC.
	 *
	 * @param npc
	 * 		The SpeakerNPC clone.
	 * @return
	 * 		The original SpeakerNPC if the clone name was found in the registered list,
	 * 		otherwise <code>null</code>.
	 */
	public SpeakerNPC getOriginal(final SpeakerNPC npc) {
		return getOriginal(npc.getName());
	}
}
