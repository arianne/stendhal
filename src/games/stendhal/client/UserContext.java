/*
 * @(#) src/games/stendhal/client/UserContext.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import java.util.HashMap;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import games.stendhal.client.events.BuddyChangeListener;
import games.stendhal.client.events.FeatureChangeListener;
import games.stendhal.client.events.RPObjectChangeListener;

/**
 * The player user context. This class holds/manages the data for the user
 * of this client. This is independent of any on-screen representation
 * Entity that, while related, serves an entirely different purpose.
 *
 * Currently this is just a helper class for StendhalClient. Maybe it will
 * be directly used by other code later.
 *
 * THIS CLASS IS STILL UNDER DEVELOPMENT/TESTING.
 */
public class UserContext implements RPObjectChangeListener {
	/**
	 * The currently known buddies
	 */
	protected HashMap<String, Boolean>	buddies;

	protected HashMap<String, String>	features;

	protected BuddyChangeListener []	buddyListeners;

	protected FeatureChangeListener []	featureListeners;

	protected int				adminlevel;


	public UserContext() {
		adminlevel = 0;
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
	 * @param	l		The listener.
	 */
	public void addBuddyChangeListener(BuddyChangeListener l) {
		BuddyChangeListener []	newListeners;


		int len = buddyListeners.length;

		newListeners = new BuddyChangeListener[len + 1];
		System.arraycopy(buddyListeners, 0, newListeners, 0, len);
		newListeners[len] = l;

		buddyListeners = newListeners;
	}


	/**
	 * Add a feature change listener.
	 *
	 * @param	l		The listener.
	 */
	public void addFeatureChangeListener(FeatureChangeListener l) {
		FeatureChangeListener []	newListeners;


		int len = featureListeners.length;

		newListeners = new FeatureChangeListener[len + 1];
		System.arraycopy(featureListeners, 0, newListeners, 0, len);
		newListeners[len] = l;

		featureListeners = newListeners;
	}


	/**
	 * Fire buddy added to all registered listeners.
	 *
	 * @param	buddyName	The name of the buddy.
	 */
	protected void fireBuddyAdded(String buddyName) {
		BuddyChangeListener [] listeners = buddyListeners;

//System.err.println("fireBuddyAdded() - buddyName = " + buddyName);
		for(BuddyChangeListener l : listeners) {
			l.buddyAdded(buddyName);
		}
	}


	/**
	 * Fire buddy offline to all registered listeners.
	 *
	 * @param	buddyName	The name of the buddy.
	 */
	protected void fireBuddyOffline(String buddyName) {
		BuddyChangeListener [] listeners = buddyListeners;

		for(BuddyChangeListener l : listeners) {
			l.buddyOffline(buddyName);
		}
	}


	/**
	 * Fire buddy online to all registered listeners.
	 *
	 * @param	buddyName	The name of the buddy.
	 */
	protected void fireBuddyOnline(String buddyName) {
		BuddyChangeListener [] listeners = buddyListeners;

		for(BuddyChangeListener l : listeners) {
			l.buddyOnline(buddyName);
		}
	}


	/**
	 * Fire buddy removed to all registered listeners.
	 *
	 * @param	buddyName	The name of the buddy.
	 */
	protected void fireBuddyRemoved(String buddyName) {
		BuddyChangeListener [] listeners = buddyListeners;

//System.err.println("fireBuddyRemoved() - buddyName = " + buddyName);
		for(BuddyChangeListener l : listeners) {
			l.buddyRemoved(buddyName);
		}
	}


	/**
	 * Fire feature enabled to all registered listeners.
	 *
	 * @param	name		The name of the feature.
	 */
	protected void fireFeatureDisabled(String name) {
		FeatureChangeListener [] listeners = featureListeners;

		for(FeatureChangeListener l : listeners) {
			l.featureDisabled(name);
		}
	}


	/**
	 * Fire feature enabled to all registered listeners.
	 *
	 * @param	name		The name of the feature.
	 * @param	value		The optional feature value.
	 */
	protected void fireFeatureEnabled(String name, String value) {
		FeatureChangeListener [] listeners = featureListeners;

		for(FeatureChangeListener l : listeners) {
			l.featureEnabled(name, value);
		}
	}


	/**
	 * Get the admin level.
	 *
	 * @return	The admin level.
	 */
	public int getAdminLevel() {
		return adminlevel;
	}


	/**
	 * Determine if the user is an admin.
	 *
	 * @return	<code>true</code> is the user is an admin.
	 */
	public boolean isAdmin() {
		return (getAdminLevel() != 0);
	}


	/**
	 * Remove a buddy change listener.
	 *
	 * @param	l		The listener.
	 */
	public void removeBuddyChangeListener(BuddyChangeListener l) {
	}


	/**
	 * Remove a feature change listener.
	 *
	 * @param	l		The listener.
	 */
	public void removeFeatureChangeListener(FeatureChangeListener l) {
	}


	/**
	 * A buddy list object added/changed attribute(s).
	 *
	 * @param	changes		The object changes.
	 */
	protected void processBuddiesAdded(final RPObject changes) {
		for(String key : changes) {
			/*
			 * FIXME60 - Remove underscore prefix before DB reset.
			 */
			if (!key.startsWith("_")) {
				continue;
			}

			String buddyName = key.substring(1);

			Boolean online = (changes.getInt(key) != 0)
				? Boolean.TRUE : Boolean.FALSE;

			if(!buddies.containsKey(buddyName)) {
				buddies.put(buddyName, online);
				fireBuddyAdded(buddyName);

				if(online == Boolean.TRUE) {
					fireBuddyOnline(buddyName);
				}
			} else if(buddies.get(buddyName) != online) {
				if(online == Boolean.TRUE) {
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
	 * @param	changes		The object changes.
	 */
	protected void processBuddiesRemoved(final RPObject changes) {
		for(String key : changes) {
			/*
			 * FIXME60 - Remove underscore prefix before DB reset.
			 */
			if (!key.startsWith("_")) {
				continue;
			}

			String buddyName = key.substring(1);

			if(buddies.remove(buddyName) != null) {
				fireBuddyRemoved(buddyName);
			}
		}
	}


	/**
	 * A feature object added/changed attribute(s).
	 *
	 * @param	changes		The object changes.
	 */
	protected void processFeatureAdded(final RPObject changes) {
		for(String name : changes) {
			if(!features.containsKey(name)) {
				String value = changes.get(name);

				features.put(name, value);
				fireFeatureEnabled(name, value);
			}
		}
	}


	/**
	 * A feature object removed attribute(s).
	 *
	 * @param	changes		The object changes.
	 */
	protected void processFeatureRemoved(final RPObject changes) {
		for(String name : changes) {
			if(features.containsKey(name)) {
				features.remove(name);
				fireFeatureDisabled(name);
			}
		}
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * An object was added.
	 *
	 * @param	object		The object.
	 */
	public void onAdded(final RPObject object) {
		if(object.has("adminlevel")) {
			adminlevel = object.getInt("adminlevel");
//			fireAdminLevelChanged(adminlevel);
		}


		if(object.hasSlot("!buddy")) {
			RPSlot slot = object.getSlot("!buddy");

			if(slot.size() != 0) {
				processBuddiesAdded(slot.getFirst());
			}
		}
	}


	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		if(changes.has("adminlevel")) {
			adminlevel = changes.getInt("adminlevel");
//			fireAdminLevelChanged(adminlevel);
		}
	}


	/**
	 * A slot object added/changed attribute(s).
	 *
	 * @param	container	The base container object.
	 * @param	slotName	The container's slot name.
	 * @param	object		The base slot object.
	 * @param	changes		The slot changes.
	 */
	public void onChangedAdded(final RPObject container, final String slotName, final RPObject object, final RPObject changes) {
//System.err.println("onChangedAdded() - slotName = " + slotName);

		if(slotName.equals("!buddy")) {
			processBuddiesAdded(changes);
		}

	}


	/**
	 * An object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		if(changes.has("adminlevel")) {
			adminlevel = 0;
//			fireAdminLevelChanged(adminlevel);
		}
	}


	/**
	 * A slot object removed attribute(s).
	 *
	 * @param	container	The base container object.
	 * @param	slotName	The container's slot name.
	 * @param	object		The base slot object.
	 * @param	changes		The slot changes.
	 */
	public void onChangedRemoved(final RPObject container, final String slotName, final RPObject object, final RPObject changes) {
		if(slotName.equals("!buddy")) {
			processBuddiesRemoved(changes);
		}
	}


	/**
	 * An object was removed.
	 *
	 * @param	object		The object.
	 */
	public void onRemoved(final RPObject object) {
		adminlevel = 0;
//		fireAdminLevelChanged(adminlevel);


		/*
		 * When object goes away, buddies also go
		 */
		for(String buddyName : buddies.keySet()) {
			fireBuddyRemoved(buddyName);
		}

		buddies.clear();
	}
}
