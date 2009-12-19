/*
 * @(#) src/games/stendhal/client/UserContext.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import games.stendhal.client.events.BuddyChangeListener;
import games.stendhal.client.events.FeatureChangeListener;
import games.stendhal.client.events.RPObjectChangeListener;

import java.util.HashMap;

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
	 * The buddy change listeners.
	 */
	protected BuddyChangeListener[] buddyListeners;

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
		buddyListeners = new BuddyChangeListener[0];
		featureListeners = new FeatureChangeListener[0];
	}

	//
	// UserContext
	//

	/**
	 * Add a buddy change listener.
	 * 
	 * @param l
	 *            The listener.
	 */
	public void addBuddyChangeListener(final BuddyChangeListener l) {
		BuddyChangeListener[] newListeners;

		final int len = buddyListeners.length;

		newListeners = new BuddyChangeListener[len + 1];
		System.arraycopy(buddyListeners, 0, newListeners, 0, len);
		newListeners[len] = l;

		buddyListeners = newListeners;
	}

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
	 * Fire buddy added to all registered listeners.
	 * 
	 * @param buddyName
	 *            The name of the buddy.
	 */
	protected void fireBuddyAdded(final String buddyName) {
		final BuddyChangeListener[] listeners = buddyListeners;

		logger.debug("Buddy added = " + buddyName);

		for (final BuddyChangeListener l : listeners) {
			l.buddyAdded(buddyName);
		}
	}

	/**
	 * Fire buddy offline to all registered listeners.
	 * 
	 * @param buddyName
	 *            The name of the buddy.
	 */
	protected void fireBuddyOffline(final String buddyName) {
		final BuddyChangeListener[] listeners = buddyListeners;

		logger.debug("Buddy offline = " + buddyName);

		for (final BuddyChangeListener l : listeners) {
			l.buddyOffline(buddyName);
		}
	}

	/**
	 * Fire buddy online to all registered listeners.
	 * 
	 * @param buddyName
	 *            The name of the buddy.
	 */
	protected void fireBuddyOnline(final String buddyName) {
		final BuddyChangeListener[] listeners = buddyListeners;

		logger.debug("Buddy online = " + buddyName);

		for (final BuddyChangeListener l : listeners) {
			l.buddyOnline(buddyName);
		}
	}

	/**
	 * Fire buddy removed to all registered listeners.
	 * 
	 * @param buddyName
	 *            The name of the buddy.
	 */
	protected void fireBuddyRemoved(final String buddyName) {
		final BuddyChangeListener[] listeners = buddyListeners;

		logger.debug("Buddy removed = " + buddyName);

		for (final BuddyChangeListener l : listeners) {
			l.buddyRemoved(buddyName);
		}
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
	 * Remove a buddy change listener.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void removeBuddyChangeListener(final BuddyChangeListener listener) {
		BuddyChangeListener[] newListeners;
		int idx;

		idx = buddyListeners.length;

		while (idx-- != 0) {
			if (buddyListeners[idx] == listener) {
				newListeners = new BuddyChangeListener[buddyListeners.length - 1];

				if (idx != 0) {
					System.arraycopy(buddyListeners, 0, newListeners, 0, idx);
				}

				if (++idx != buddyListeners.length) {
					System.arraycopy(buddyListeners, idx, newListeners,
							idx - 1, buddyListeners.length - idx);
				}

				buddyListeners = newListeners;
				break;
			}
		}
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
	 * A buddy list object added/changed attribute(s).
	 * 
	 * @param changes
	 *            The object changes.
	 */
	protected void processBuddiesAdded(final RPObject changes) {
		for (final String key : changes) {
			/*
			 * TODO: Drop underscore prefix when 'id' is not forced into the
			 * RPObject attributes
			 */
			logger.debug(key);

			if ("id".equals(key)) {
				continue;
			}

			final String buddyName = key.substring(1);

			final boolean online = (changes.getInt(key) != 0);

			if (!buddies.containsKey(buddyName)) {
				buddies.put(buddyName, Boolean.valueOf(online));
				fireBuddyAdded(buddyName);

				if (online) {
					fireBuddyOnline(buddyName);
				}
			} else if (buddies.get(buddyName).booleanValue() != online) {
				if (online) {
					fireBuddyOnline(buddyName);
				} else {
					fireBuddyOffline(buddyName);
				}
			}
		}
	}

	/**
	 * A buddy list object removed attribute(s).
	 * 
	 * @param changes
	 *            The object changes.
	 */
	protected void processBuddiesRemoved(final RPObject changes) {
		for (final String key : changes) {
			/*
			 * TODO: Drop underscore prefix when 'id' is not forced into the
			 * RPObject attributes
			 */
			if ("id".equals(key)) {
				continue;
			}

			final String buddyName = key.substring(1);

			if (buddies.remove(buddyName) != null) {
				fireBuddyRemoved(buddyName);
			}
		}
	}

	/**
	 * A feature object added/changed attribute(s).
	 * 
	 * @param changes
	 *            The object changes.
	 */
	protected void processFeaturesAdded(final RPObject changes) {
		for (final String featureName : changes) {
			// Skip internal ID field
			if ("id".equals(featureName)) {
				continue;
			}
			
			if (!features.containsKey(featureName)) {
				final String value = changes.get(featureName);

				features.put(featureName, value);
				fireFeatureEnabled(featureName, value);
			}
		}
	}

	/**
	 * A feature object removed attribute(s).
	 * 
	 * @param changes
	 *            The object changes.
	 */
	protected void processFeaturesRemoved(final RPObject changes) {
		for (final String featureName : changes) {
			// Skip internal ID field
			if ("id".equals(featureName)) {
				continue;
			}

			if (features.containsKey(featureName)) {
				features.remove(featureName);
				fireFeatureDisabled(featureName);
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
		if (isUser(object)) {
			if (slotName.equals("!buddy")) {
				processBuddiesAdded(sobject);
			}
		}
		if (slotName.equals("!features")) {
			processFeaturesAdded(sobject);
		}
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
		if (isUser(object)) {
			if ("!buddy".equals(slotName)) {
				processBuddiesAdded(schanges);
			}
		}
		if ("!features".equals(slotName)) {
			processFeaturesAdded(schanges);
		}
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
		if (isUser(object)) {
			if ("!buddy".equals(slotName)) {
				processBuddiesRemoved(schanges);
			} else if ("!features".equals(slotName)) {
				processFeaturesRemoved(schanges);
			}
		}
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
		if (isUser(object)) {
			if ("!buddy".equals(slotName)) {
				processBuddiesRemoved(sobject);
			} else if ("!features".equals(slotName)) {
				processFeaturesRemoved(sobject);
			}
		}
	}

	public void setName(final String username) {
		name = username;

	}
}
