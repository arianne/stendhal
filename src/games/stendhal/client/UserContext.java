/*
 * @(#) src/games/stendhal/client/UserContext.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import games.stendhal.client.listener.FeatureChangeListener;
import games.stendhal.client.listener.RPObjectChangeListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

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

	/**
	 * The currently known buddies.
	 */
	protected HashMap<String, Boolean> buddies;

	/**
	 * The currently enabled features.
	 */
	protected HashMap<String, String> features;

	/**
	 * The feature change listeners.
	 */
	protected FeatureChangeListener[] featureListeners;

	/**
	 * The admin level.
	 */
	protected int adminlevel;

	/**
	 * The player character's name.
	 */
	protected String name;

	/**
	 * The owned sheep RPObject ID.
	 */
	protected int sheepID;

	private RPObject player;

	/**
	 * Constructor.
	 * 
	 */
	public UserContext() {

		adminlevel = 0;
		name = null;
		sheepID = 0;
		buddies = new HashMap<String, Boolean>();
		features = new HashMap<String, String>();
		featureListeners = new FeatureChangeListener[0];
	}

	//
	// UserContext
	//

	/**
	 * Add a feature change listener.
	 * 
	 * @param l
	 *            The listener.
	 */
	public void addFeatureChangeListener(final FeatureChangeListener l) {
		FeatureChangeListener[] newListeners;

		final int len = featureListeners.length;

		newListeners = new FeatureChangeListener[len + 1];
		System.arraycopy(featureListeners, 0, newListeners, 0, len);
		newListeners[len] = l;

		featureListeners = newListeners;
	}



	/**
	 * Fire feature enabled to all registered listeners.
	 * 
	 * @param name
	 *            The name of the feature.
	 */
	protected void fireFeatureDisabled(final String name) {
		final FeatureChangeListener[] listeners = featureListeners;

		logger.debug("Feature disabled: " + name);

		for (final FeatureChangeListener l : listeners) {
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
	protected void fireFeatureEnabled(final String name, final String value) {
		final FeatureChangeListener[] listeners = featureListeners;

		logger.debug("Feature enabled: " + name + " = " + value);

		for (final FeatureChangeListener l : listeners) {
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
	public void removeFeatureChangeListener(final FeatureChangeListener listener) {
		FeatureChangeListener[] newListeners;
		int idx;

		idx = featureListeners.length;

		while (idx-- != 0) {
			if (featureListeners[idx] == listener) {
				newListeners = new FeatureChangeListener[featureListeners.length - 1];

				if (idx != 0) {
					System.arraycopy(featureListeners, 0, newListeners, 0, idx);
				}

				if (++idx != featureListeners.length) {
					System.arraycopy(featureListeners, idx, newListeners,
							idx - 1, featureListeners.length - idx);
				}

				featureListeners = newListeners;
				break;
			}
		}
	}


	/**
	 * A feature object added/changed attribute(s).
	 * 
	 * @param changes
	 *            The object changes.
	 */
	protected void processFeaturesAdded(final Map<String, String> changes) {
		for (final Entry<String, String> entry : changes.entrySet()) {
			if (!features.containsKey(entry.getKey())) {
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
	protected void processFeaturesRemoved(final Map<String, String> changes) {
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

	public boolean isUser(final RPObject object) {
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
	public void onSlotRemoved(final RPObject object, final String slotName,
			final RPObject sobject) {
	}

	public void setName(final String username) {
		name = username;
	}
}
