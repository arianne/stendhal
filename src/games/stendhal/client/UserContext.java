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
package games.stendhal.client;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import games.stendhal.client.listener.FeatureChangeListener;
import games.stendhal.client.listener.RPObjectChangeListener;
import marauroa.common.game.RPObject;

/**
 * The player user context. This class holds/manages the data for the user of
 * this client. This is independent of any on-screen representation Entity that,
 * while related, serves an entirely different purpose.
 *
 * Currently this is just a helper class for StendhalClient. Maybe it will be
 * directly used by other code later.
 */
public class UserContext implements RPObjectChangeListener {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(UserContext.class);

	private static final UserContext instance = new UserContext();

	/**
	 * The currently enabled features.
	 */
	private final HashMap<String, String> features = new HashMap<>();

	/**
	 * The feature change listeners.
	 */
	private final List<FeatureChangeListener> featureListeners = new CopyOnWriteArrayList<FeatureChangeListener>();

	/**
	 * The admin level.
	 */
	private int adminlevel;

	/**
	 * The player character's name.
	 */
	private String name;

	/**
	 * The owned sheep RPObject ID.
	 */
	private int sheepID;

	private volatile RPObject player;

	/**
	 * Constructor.
	 *
	 */
	private UserContext() {
	}

	//
	// UserContext
	//

	public static UserContext get() {
		return instance;
	}

	/**
	 * Add a feature change listener.
	 *
	 * @param l
	 *            The listener.
	 */
	public void addFeatureChangeListener(final FeatureChangeListener l) {
		featureListeners.add(l);
	}

	/**
	 * Fire feature enabled to all registered listeners.
	 *
	 * @param name
	 *            The name of the feature.
	 */
	private void fireFeatureDisabled(final String name) {
		logger.debug("Feature disabled: " + name);

		for (final FeatureChangeListener l : featureListeners) {
			l.featureDisabled(name);
		}
	}

	/**
	 * Fire feature enabled to all registered listeners.
	 *
	 * @param name
	 *            The name of the feature.
	 * @param value
	 *            The optional feature value.
	 */
	private void fireFeatureEnabled(final String name, final String value) {
		logger.debug("Feature enabled: " + name + " = " + value);

		for (final FeatureChangeListener l : featureListeners) {
			l.featureEnabled(name, value);
		}
	}

	/**
	 * Get the admin level.
	 *
	 * @return The admin level.
	 */
	public int getAdminLevel() {
		return adminlevel;
	}

	/**
	 * Get the player character name.
	 *
	 * @return The player character name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the player's owned sheep RPObject ID.
	 *
	 * @return The RPObject ID of the sheep the player owns, or <code>0</code>
	 *         if none.
	 */
	public int getSheepID() {
		return sheepID;
	}

	/**
	 * Determine if the user is an admin.
	 *
	 * @return <code>true</code> is the user is an admin.
	 */
	public boolean isAdmin() {
		return (getAdminLevel() != 0);
	}


	/**
	 * Remove a feature change listener.
	 *
	 * @param listener
	 *            The listener.
	 */
	void removeFeatureChangeListener(final FeatureChangeListener listener) {
		featureListeners.remove(listener);
	}


	/**
	 * A feature object added/changed attribute(s).
	 *
	 * @param changes
	 *            The object changes.
	 */
	private void processFeaturesAdded(final Map<String, String> changes) {
		for (final Entry<String, String> entry : changes.entrySet()) {
			if (!features.containsKey(entry.getKey()) || !features.get(entry.getKey()).equals(entry.getValue())) {
				features.put(entry.getKey(), entry.getValue());
				fireFeatureEnabled(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * A feature object removed attribute(s).
	 *
	 * @param changes
	 *            The object changes.
	 */
	private void processFeaturesRemoved(final Map<String, String> changes) {
		for (final String feature : changes.keySet()) {
			if (features.containsKey(feature)) {
				features.remove(feature);
				fireFeatureDisabled(feature);
			}
		}
	}

	public RPObject getPlayer() {
		return player;
	}

	protected void setPlayer(final RPObject object) {
		/*
		 * Ignore no-changes
		 */
		if (player != object) {
			player = object;
			name = object.get("name");
		}
	}

	boolean isUser(final RPObject object) {
		if (name == null) {
			return false;
		}
		if (object.getRPClass().subclassOf("player")) {
			return name.equalsIgnoreCase(object.get("name"));
		} else {
			return false;
		}
	}

	//
	// RPObjectChangeListener
	//

	/**
	 * An object was added.
	 *
	 * @param object
	 *            The object.
	 */
	@Override
	public void onAdded(final RPObject object) {
		if (isUser(object)) {

			if (object.has("adminlevel")) {
				adminlevel = object.getInt("adminlevel");
				// fireAdminLevelChanged(adminlevel);
			}
		}
	}

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		if (isUser(object)) {
			if (changes.has("adminlevel")) {
				adminlevel = changes.getInt("adminlevel");
			}

			if (changes.has("name")) {
				name = changes.get("name");
			}

			if (changes.has("sheep")) {
				sheepID = changes.getInt("sheep");
				// fireOwnedSheep(sheepID);
			}

			if (changes.hasMap("features")) {
				processFeaturesAdded(changes.getMap("features"));
			}
		}
	}


	/**
	 * An object removed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		if (isUser(object)) {
			if (changes.has("adminlevel")) {
				adminlevel = 0;
			}

			if (changes.has("name")) {
				name = null;
			}

			if (changes.has("sheep")) {
				sheepID = 0;
				// fireOwnedSheep(sheepID);
			}

			if (changes.hasMap("features")) {
				processFeaturesRemoved(changes.getMap("features"));
			}
		}
	}

	/**
	 * An object was removed.
	 *
	 * @param object
	 *            The object.
	 */
	@Override
	public void onRemoved(final RPObject object) {
		if (isUser(object)) {
			adminlevel = 0;

			name = null;

			sheepID = 0;
		}
	}

	/**
	 * A slot object was added.
	 *
	 * @param object
	 *            The container object.
	 * @param slotName
	 *            The slot name.
	 * @param sobject
	 *            The slot object.
	 */
	@Override
	public void onSlotAdded(final RPObject object, final String slotName,
			final RPObject sobject) {
	}

	/**
	 * A slot object added/changed attribute(s).
	 *
	 * @param object
	 *            The base container object.
	 * @param slotName
	 *            The container's slot name.
	 * @param sobject
	 *            The slot object.
	 * @param schanges
	 *            The slot object changes.
	 */
	@Override
	public void onSlotChangedAdded(final RPObject object,
			final String slotName, final RPObject sobject,
			final RPObject schanges) {
	}

	/**
	 * A slot object removed attribute(s).
	 *
	 * @param object
	 *            The base container object.
	 * @param slotName
	 *            The container's slot name.
	 * @param sobject
	 *            The slot object.
	 * @param schanges
	 *            The slot object changes.
	 */
	@Override
	public void onSlotChangedRemoved(final RPObject object,
			final String slotName, final RPObject sobject,
			final RPObject schanges) {
	}

	/**
	 * A slot object was removed.
	 *
	 * @param object
	 *            The container object.
	 * @param slotName
	 *            The slot name.
	 * @param sobject
	 *            The slot object.
	 */
	@Override
	public void onSlotRemoved(final RPObject object, final String slotName,
			final RPObject sobject) {
	}

	public void setName(final String username) {
		name = username;
	}

	/**
	 * Checks if the player has a feature.
	 */
	public boolean hasFeature(final String name) {
		return features.get(name) != null;
	}
}
