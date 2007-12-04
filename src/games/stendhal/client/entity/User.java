package games.stendhal.client.entity;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.WorldObjects;
import games.stendhal.client.soundreview.HearingArea;
import games.stendhal.common.FeatureList;
import games.stendhal.common.Grammar;
import games.stendhal.common.NotificationType;

import java.awt.geom.Rectangle2D;
import marauroa.common.game.RPObject;

public class User extends Player {

	private static User instance;

	/**
	 * Client features.
	 */
	private FeatureList features;

	public static boolean isNull() {

		return instance == null;

	}

	public static User get() {
		return instance;
	}

	public User() {
		super();
		instance = this;
		modificationCount = 0;
		features = new FeatureList();
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
	 * returns the modificationCount. This counter is increased each time a
	 * perception is received from the server (so all serverside changes
	 * increases the mod-count). This counters purpose is to be sure that this
	 * entity is modified or not (ie for gui elements).
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

	@Override
	public void onHealed(final int amount) {

		super.onHealed(amount);

		StendhalUI.get().addEventLine(
				getTitle() + " heals "
						+ Grammar.quantityplnoun(amount, "health point") + ".",
				NotificationType.POSITIVE);

	}

	/**
	 * the absolute world area (coordinates) where the player can possibly hear
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

	public String getFeature(String name) {
		return features.get(name);
	}

	public boolean hasFeature(String name) {
		return features.has(name);
	}

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

		if (object.has("features")) {
			features.decode(object.get("features"));
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
		super.onChangedAdded(object, changes);
		modificationCount++;

		if (changes.has("features")) {
			features.decode(changes.get("features"));
		}

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
		}
	}

	@Override
	public void onChangedRemoved(final RPObject base, final RPObject diff) {
		modificationCount++;
		super.onChangedRemoved(base, diff);

		if (diff.has("features")) {
			features.clear();
		}
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

	public static void setNull() {
		instance = null;

	}
}
