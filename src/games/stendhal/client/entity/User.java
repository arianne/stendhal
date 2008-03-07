package games.stendhal.client.entity;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.WorldObjects;
import games.stendhal.client.stendhal;
import games.stendhal.client.soundreview.HearingArea;
import games.stendhal.client.update.Version;
import games.stendhal.common.Grammar;
import games.stendhal.common.NotificationType;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

public class User extends Player {

	private static User instance;

	/**
	 * Client features.
	 */
	/*
	 * TODO remove unused code private FeatureList features;
	 */

	private String serverVersion = null;

	public static boolean isNull() {

		return instance == null;

	}

	public static User get() {
		return instance;
	}

	public User() {
		instance = this;
		modificationCount = 0;
		/*
		 * TODO remove unused code features = new FeatureList();
		 */
	}

	/**
	 * When the entity's position changed.
	 * 
	 * @param x
	 *            The new X coordinate.
	 * @param y
	 *            The new Y coordinate.
	 */
	@Override
	protected void onPosition(final double x, final double y) {
		super.onPosition(x, y);

		WorldObjects.firePlayerMoved();
		HearingArea.set(x, y);
	}

	private int modificationCount;

	/**
	 * Returns the modificationCount. This counter is increased each time a
	 * perception is received from the server (so all serverside changes
	 * increases the mod-count). This counter's purpose is to be sure that this
	 * entity is modified or not (ie for gui elements).
	 * @return a number representing the amount of changes.
	 */
	public long getModificationCount() {
		return modificationCount;
	}

	@Override
	protected void onAway(final String message) {
		super.onAway(message);

		StendhalUI.get().addEventLine(
				(message != null) ? "You have been marked as being away."
						: "You are no longer marked as being away.",
				NotificationType.INFORMATION);
	}

	public static boolean isAdmin() {
		if (isNull()) {
			return false;
		}
		User me = User.get();
		if (me.rpObject == null) {
			return false;
		}

		return me.rpObject.has("adminlevel")
				&& (me.rpObject.getInt("adminlevel") >= 600);
	}

	public int getObjectID() {
		return rpObject.getID().getObjectID();
	}

	public boolean hasSheep() {
		if (rpObject == null) {
			return false;
		}
		return rpObject.has("sheep");
	}

	public boolean hasPet() {
		if (rpObject == null) {
			return false;
		}
		return rpObject.has("pet");
	}
	
	public int getSheepID() {
		return rpObject.getInt("sheep");
	}
	
	public int getPetID() {
		return rpObject.getInt("pet");
	}

	@Override
	public void onHealed(final int amount) {
		super.onHealed(amount);

		StendhalUI.get().addEventLine(
				getTitle() + " heals "
						+ Grammar.quantityplnoun(amount, "health point") + ".",
				NotificationType.POSITIVE);
	}

	/**
	 * The absolute world area (coordinates) where the player can possibly hear.
	 * sounds
	 * 
	 * @return Rectangle2D area
	 */
	public Rectangle2D getHearingArea() {
		final double HEARING_RANGE = 20;
		double width = HEARING_RANGE * 2;
		return new Rectangle2D.Double(getX() - HEARING_RANGE, getY()
				- HEARING_RANGE, width, width);
	}

	/*
	 * TODO remove unused code public String getFeature(String name) { return
	 * features.get(name); }
	 * 
	 * public boolean hasFeature(String name) { return features.has(name); }
	 */

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param object
	 *            The object.
	 * 
	 * @see-also #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		/*
		 * TODO remove unused code if (object.has("features")) {
		 * features.decode(object.get("features")); }
		 */
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
		super.onChangedAdded(object, changes);
		modificationCount++;

		/*
		 * TODO remove unused code if (changes.has("features")) {
		 * features.decode(changes.get("features")); }
		 */

		// The first time we ignore it.
		if (object != null) {
			if (changes.has("online")) {
				String[] players = changes.get("online").split(",");
				for (String playerName : players) {
					StendhalUI.get().addEventLine(
							playerName + " has joined Stendhal.",
							NotificationType.INFORMATION);
				}
			}

			if (changes.has("offline")) {
				String[] players = changes.get("offline").split(",");
				for (String playername : players) {
					StendhalUI.get().addEventLine(
							playername + " has left Stendhal.",
							NotificationType.INFORMATION);
				}
			}

			if (changes.has("release")) {
				serverVersion = changes.get("release");
				if (!Version.checkCompatibility(serverVersion,
						stendhal.VERSION)) {
					StendhalUI.get().addEventLine(
							"Your client may not function properly.\nThe version of this server is "
									+ serverVersion
									+ " but your client is version "
									+ stendhal.VERSION
									+ ".\nPlease download the new version from http://arianne.sourceforge.net",
							NotificationType.ERROR);
				}
			}
		}
	}

	@Override
	public void onChangedRemoved(final RPObject base, final RPObject diff) {
		modificationCount++;
		super.onChangedRemoved(base, diff);

		/*
		 * TODO remove unused code if (diff.has("features")) { features.clear(); }
		 */
	}

	/**
	 * Returns true when the entity was modified since the
	 * <i>oldModificationCount</i>.
	 * 
	 * @param oldModificationCount
	 *            the old modificationCount
	 * @return true when the entity was modified, false otherwise
	 * @see #getModificationCount()
	 */
	public boolean isModified(final long oldModificationCount) {
		return oldModificationCount != modificationCount;
	}

	/**
	 * resets the class to uninitialized.
	 */
	public static void setNull() {
		instance = null;
	}

	/**
	 * query the version of the server we are currently connected to.
	 * 
	 * @return server version string
	 */
	public String getServerVersion() {
		return serverVersion;
	}

	/**
	 * Returns the objectid for the named item.
	 * 
	 * @param slotName
	 *            name of slot to search
	 * @param itemName
	 *            name of item
	 * @return objectid or <code>-1</code> in case there is no such item
	 */
	public int findItem(String slotName, String itemName) {
		for (RPObject item : getSlot(slotName)) {
			if (item.get("name").equals(itemName)) {
				int itemID = item.getID().getObjectID();

				return itemID;
			}
		}

		return -1;
    }
}
