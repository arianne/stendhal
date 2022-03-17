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

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;


/**
 * Manages registered SpeakerNPC clones.
 */
public class CloneManager {

	private static final Logger logger = Logger.getLogger(CloneManager.class);

	/** The singleton instance. */
	private static CloneManager instance;

	private static final Map<String, List<String>> clonedList = new HashMap<>();


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 *     CloneManager instance.
	 */
	public static CloneManager get() {
		if (instance == null) {
			instance = new CloneManager();
		}

		return instance;
	}

	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 *     CloneManager instance.
	 * @deprecated
	 *     Use CloneManager.get().
	 */
	@Deprecated
	public static CloneManager getInstance() {
		return get();
	}

	/**
	 * Hidden singleton constructor.
	 */
	private CloneManager() {
		// singleton
	}

	/**
	 * Registers a clone name as cloned from original name.
	 *
	 * If the clone name is <code>null</code> the original NPC's name
	 * will be used with an index number appended.
	 *
	 * @param origName
	 *     Name of the SpeakerNPC that has been cloned.
	 * @param cloneName
	 *     Name to be registered as a clone.
	 * @return
	 *     <code>true</code> if name registration succeeded.
	 */
	private boolean register(final String origName, String cloneName) {
		// the list of clones of this original entity
		List<String> registeredClones = clonedList.get(origName);

		int idx;
		if (registeredClones == null) {
			idx = 2;
			registeredClones = new ArrayList<String>();
			clonedList.put(origName, registeredClones);
		} else {
			idx = registeredClones.size() + 2;
		}

		if (cloneName == null) {
			cloneName = origName + Integer.toString(idx);
		}

		if (!registeredClones.contains(cloneName)) {
			registeredClones.add(cloneName);
			return true;
		}

		logger.error("clone name already registered: " + cloneName);
		return false;
	}

	/**
	 * Registers a clone name as cloned from original name.
	 *
	 * The new clone's name will be the original NPC's name with an
	 * index number appended.
	 *
	 * @param origName
	 *     Name of the SpeakerNPC that has been cloned.
	 * @return
	 *     <code>true</code> if name registration succeeded.
	 */
	/*
	private boolean register(final String origName) {
		return register(origName, null);
	}
	*/

	/**
	 * Removes a clone name from registry.
	 *
	 * @param origName
	 *     Name of the originally cloned NPC.
	 * @param cloneName
	 *     Clone name to be removed.
	 * @return
	 *     <code>true</code> if removal was successful, <code>false</code> otherwise
	 *     or if original NPC did not have any clones.
	 */
	private boolean unregister(final String origName, final String cloneName) {
		final List<String> registeredClones = clonedList.get(origName);
		if (registeredClones == null) {
			return false;
		}

		registeredClones.remove(cloneName);

		return !registeredClones.contains(cloneName);
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
			// FIXME: clones using the same name can be added to world
			clone = new SpeakerNPC(cloneName) {
				@Override
				public void onAdded(final StendhalRPZone zone) {
					super.onAdded(zone);
					register(origName, cloneName);
				}

				@Override
				public void onRemoved(final StendhalRPZone zone) {
					super.onRemoved(zone);
					unregister(origName, cloneName);
				}
			};

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

			// clones are displayed with name of original by default
			clone.setTitle(origName);

			// clones should not be displayed on website, but check for alternative image just to be safe
			clone.setAlternativeImage(orig.getAlternativeImage());
		}

		if (clone == null) {
			if (orig == null) {
				logger.warn("attempted to clone null SpeakerNPC");
			} else {
				logger.warn("failed to clone SpeakerNPC: " + orig.getName());
			}
		} else if (orig != null) {
			logger.debug("cloned SpeakerNPC: " + orig.getName() + " (" + clone.getName() + ")");
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
	 * Registeres an existing SpeakerNPC as a clone of another.
	 *
	 * This only sets the "cloned" attribute for the clone entity.
	 * Any other attributes must be set manually.
	 *
	 * @param orig
	 *     SpeakerNPC entity to be cloned.
	 * @param clone
	 *     SpeakerNPC entity to be registered as a clone.
	 */
	public void registerAsClone(final SpeakerNPC orig, final SpeakerNPC clone) {
		final String origName = orig.getName();
		if (!register(origName, clone.getName())) {
			// abort if name registration fails
			return;
		}

		clone.put("cloned", origName);
	}

	/**
	 * Registeres an existing SpeakerNPC as a clone of another.
	 *
	 * This only sets the "cloned" attribute for the clone entity.
	 * Any other attributes must be set manually.
	 *
	 * @param origName
	 *     Name of the SpeakerNPC to be cloned.
	 * @param cloneName
	 *     Name to be registered as a clone.
	 */
	public void registerAsClone(final String origName, final String cloneName) {
		final SpeakerNPC clone = SingletonRepository.getNPCList().get(cloneName);
		if (!register(origName, cloneName)) {
			// abort if name registration fails
			return;
		}

		clone.put("cloned", origName);
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
		// FIXME: this will fail if clone is not currently added to world
		for (final List<String> clones: clonedList.values()) {
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

		for (final String key: clonedList.keySet()) {
			if (clonedList.get(key).contains(name)) {
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
