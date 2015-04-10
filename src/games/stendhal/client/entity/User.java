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
package games.stendhal.client.entity;

import static games.stendhal.common.constants.Actions.MODE;
import static games.stendhal.common.constants.Actions.TYPE;
import static games.stendhal.common.constants.Actions.WALK;
import static games.stendhal.common.constants.Common.AUTOWALK;
import static games.stendhal.common.constants.Common.PATHSET;
import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameObjects;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.Testing;
import games.stendhal.common.grammar.Grammar;

import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * This class identifies the user of this client.
 *
 * @author durkham, hendrik
 */
public class User extends Player {
	/* The logger instance. */
	private static final Logger logger = Logger.getLogger(User.class);

	private static User instance;
	private static String groupLootmode;
	private static Set<String> groupMembers;
	private final HashSet<String> ignore = new HashSet<String>();
	private final SpeedPredictor speedPredictor;

	/**
	 * is the user object not set, yet?
	 *
	 * @return true, if the the user object is unknown; false if it is known
	 */
	public static boolean isNull() {
		return instance == null;
	}

	/**
	 * gets the User object
	 *
	 * @return user object
	 */
	public static User get() {
		return instance;
	}

	/**
	 * creates a User object
	 */
	public User() {
		if (instance == null) {
			speedPredictor = new SpeedPredictor();
		} else {
			speedPredictor = new SpeedPredictor(instance.speedPredictor);
		}
		instance = this;
	}

	@Override
	protected void onAway(final String message) {
		super.onAway(message);

		String text;
		if (message == null) {
			text = "You are no longer marked as being away.";
		} else {
			text = "You have been marked as being away.";
		}
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(text, NotificationType.INFORMATION));
	}

	/**
	 * is this user an admin with an adminlevel equal or above 600?
	 *
	 * @return true, if the user is an admin; false otherwise
	 */
	public static boolean isAdmin() {
		if (isNull()) {
			return false;
		}

		final User me = User.get();
		if (me.rpObject == null) {
			return false;
		}

		return me.rpObject.has("adminlevel")
				&& (me.rpObject.getInt("adminlevel") >= 600);
	}

	/**
	 * gets the level of the current user
	 *
	 * @return level
	 */
	public static int getPlayerLevel() {
		if (!isNull()) {
			final User me = User.get();

			if (me.rpObject != null) {
				return me.getLevel();
			}
		}

		return 0;
	}

	/**
	 * checks whether the user owns a sheep
	 *
	 * @return true, if the user owns a sheep; false otherwise
	 */
	public boolean hasSheep() {
		if (rpObject == null) {
			return false;
		}
		return rpObject.has("sheep");
	}

	/**
	 * checks whether the user owns a pet
	 *
	 * @return true, if the user owns a pet; false otherwise
	 */
	public boolean hasPet() {
		if (rpObject == null) {
			return false;
		}
		return rpObject.has("pet");
	}

	/**
	 * gets the ID of a sheep
	 *
	 * @return ID of sheep
	 */
	public int getSheepID() {
		return rpObject.getInt("sheep");
	}

	/**
	 * gets the ID of a pet
	 *
	 * @return ID of pet
	 */
	public int getPetID() {
		return rpObject.getInt("pet");
	}

	@Override
	public void onHealed(final int amount) {
		super.onHealed(amount);
		ClientSingletonRepository.getUserInterface().addEventLine(
				new HeaderLessEventLine(
						getTitle() + " heals "
						+ Grammar.quantityplnoun(amount, "health point") + ".",
						NotificationType.HEAL));
	}

	/**
	 * The absolute world area (coordinates) where the player can possibly hear.
	 * sounds
	 *
	 * @return Rectangle2D area
	 */
	public Rectangle2D getHearingArea() {
		final double HEARING_RANGE = 20;
		final double width = HEARING_RANGE * 2;
		return new Rectangle2D.Double(getX() - HEARING_RANGE, getY()
				- HEARING_RANGE, width, width);
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
		/* TODO: Remove condition when walking bug fix is finished. */
		if (false) { // DISABLED
			if (!this.stopped()) {
				boolean shouldStop = true;
				String debugString = "Stopped on:";

				if (StendhalClient.get().directionKeyIsPressed()) {
					shouldStop = false;
				} else {
					debugString += " !directionKeyIsPressed()";
				}
				if (object.has(AUTOWALK)) {
					shouldStop = false;
				} else {
					debugString += " !has(AUTOWALK)";
				}
				if (object.has(PATHSET)) {
					shouldStop = false;
				} else {
					debugString += " !has(PATHSET)";
				}

				if (shouldStop) {
					/* Stop the character's movement. */
					this.stopMovement();

					if (logger.isDebugEnabled() || Testing.DEBUG) {
						logger.info(debugString);
					}
				}
			}
		}

		super.onChangedAdded(object, changes);

		// The first time we ignore it.
		if (object != null) {
			if (changes.has("offline")) {
				final String[] players = changes.get("offline").split(",");
				for (final String playername : players) {
						ClientSingletonRepository.getUserInterface().addEventLine(
							new HeaderLessEventLine(
							playername + " has left Stendhal.",
							NotificationType.INFORMATION));
				}
			}

			if (changes.has("online")) {
				final String[] players = changes.get("online").split(",");
				for (final String playerName : players) {
					ClientSingletonRepository.getUserInterface().addEventLine(
							new HeaderLessEventLine(
							playerName + " has joined Stendhal.",
							NotificationType.INFORMATION));
				}
			}

			if (changes.hasSlot("!ignore")) {
				RPObject ign = changes.getSlot("!ignore").getFirst();
				if (ign != null) {
					addIgnore(ign);
				}
			}
		}
	}

	@Override
	public void onChangedRemoved(final RPObject base, final RPObject diff) {
		super.onChangedRemoved(base, diff);
		if (diff.hasSlot("!ignore")) {
			RPObject ign = diff.getSlot("!ignore").getFirst();
			if (ign != null) {
				removeIgnore(ign);
			}
		}
	}

	/**
	 * Resets the class to uninitialized.
	 */
	static void setNull() {
		instance = null;
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
	public int findItem(final String slotName, final String itemName) {
		RPSlot slot = getSlot(slotName);
		if (slot == null) {
			return -1;
		}
		for (final RPObject item : slot) {
			if (item.get("name").equals(itemName)) {
				final int itemID = item.getID().getObjectID();

				return itemID;
			}
		}

		return -1;
	}

	/**
	 * Is this object the user of this client?
	 *
	 * @return true
	 */
	@Override
	public boolean isUser() {
		return true;
	}

	/**
	 * calculates the squared distance between the user and the specified coordinates
	 *
	 * @param x2 x coordinate
	 * @param y2 y coordinate
	 * @return the squared distance
	 */
	static double squaredDistanceTo(final double x2, final double y2) {
		if (User.isNull()) {
			return Double.POSITIVE_INFINITY;
		}
		return (User.get().getX() - x2) * (User.get().getX() - x2)
				+ (User.get().getY() - y2) * (User.get().getY() - y2);
	}

	/**
	 * is the named player ignored?
	 *
	 * @param name name of player
	 * @return true, if the player should be ignored; false otherwise
	 */
	public static boolean isIgnoring(String name) {
		if (User.isNull()) {
			return false;
		}

		return User.get().ignore.contains(name);
	}

	/**
	 * is the specified charname a buddy of us?
	 *
	 * @param name charname to test
	 * @return true, if it is a buddy, false if it is not a buddy or the user object is unknown.
	 */
	public static boolean hasBuddy(String name) {
		if (User.isNull()) {
			return false;
		}

		RPObject rpobject = User.get().rpObject;
		return rpobject.has("buddies", name);
	}

	/**
	 * gets the server release version
	 *
	 * @return server release version or <code>null</code>
	 */
	public static String getServerRelease() {
		if (User.isNull()) {
			return null;
		}

		return User.get().rpObject.get("release");
	}

	/**
	 * gets the name of the player's character
	 *
	 * @return charname or <code>null</code>
	 */
	public static String getCharacterName() {
		if (User.isNull()) {
			return null;
		}
		return User.get().getName();
	}

	/**
	 * Add players to the set of ignored players.
	 * Player names are the attributes prefixed with '_'.
	 *
	 * @param ignoreObj The container object for player names
	 */
	private void addIgnore(RPObject ignoreObj) {
		for (String attr : ignoreObj) {
			if (attr.charAt(0) == '_') {
				ignore.add(attr.substring(1));
			}
		}
	}

	/**
	 * Remove players from the set of ignored players.
	 * Player names are the attributes prefixed with '_'.
	 *
	 * @param ignoreObj The container object for player names
	 */
	private void removeIgnore(RPObject ignoreObj) {
		for (String attr : ignoreObj) {
			if (attr.charAt(0) == '_') {
				ignore.remove(attr.substring(1));
			}
		}
	}

	/**
	 * is the player in a group which shares the loot?
	 *
	 * @return true if this player is a group and it uses shared looting
	 */
	public static boolean isGroupSharingLoot() {
		return groupLootmode != null && groupLootmode.equals("shared");
	}

	/**
	 * checks if the specified player is in the same group as this player
	 *
	 * @param otherPlayer name of the other player
	 * @return true if the other player is in the same group
	 */
	public static boolean isPlayerInGroup(String otherPlayer) {
		if (groupMembers == null) {
			return false;
		}
		return groupMembers.contains(otherPlayer);
	}

	/**
	 * updates the group information
	 *
	 * @param members members
	 * @param lootmode lootmode
	 */
	public static void updateGroupStatus(List<String> members, String lootmode) {
		Set<String> oldGroupMembers = User.groupMembers;

		if (members == null) {
			User.groupMembers = null;
		} else {
			User.groupMembers = new HashSet<String>(members);
		}
		User.groupLootmode = lootmode;

		// fire change event to color of player object on minimap
		for (IEntity entity : GameObjects.getInstance()) {
			if (entity instanceof Player) {
				if (((oldGroupMembers != null) && oldGroupMembers.contains(entity.getName()))
						|| ((User.groupMembers != null) && User.groupMembers.contains(entity.getName()))) {
					((Player) entity).fireChange(RPEntity.PROP_GROUP_MEMBERSHIP);
				}
			}
		}
	}

	/**
	 * gets the zone name
	 *
	 * @return zone name
	 */
	public String getZoneName() {
		return rpObject.getID().getZoneID();
	}

	@Override
	protected void processPositioning(final RPObject base, final RPObject diff) {
		if (speedPredictor.isActive() && (diff.has("direction") || diff.has("x") || diff.has("y"))) {
			speedPredictor.onMoved();
		}
		super.processPositioning(base, diff);
	}

	/**
	 * Start movement towards a direction. This is for
	 * the client side movement prediction to start moving before the server
	 * responds to the move action.
	 *
	 * @param direction new direction
	 * @param facing <code>true</code> if the player should just turn
	 */
	public void predictMovement(Direction direction, boolean facing) {
		// Only handle the case of starting movement. Prediction when already
		// moving looks odd.
		if (stopped()) {
			if (isConfused()) {
				direction = direction.oppositeDirection();
			}
			if (!facing) {
				double speed = speedPredictor.getSpeed();
				setSpeed(direction.getdx() * speed, direction.getdy() * speed);
				fireChange(PROP_SPEED);
				speedPredictor.startPrediction();
			}
			// setDirection fires the appropriate property for itself
			setDirection(direction);
		}
	}

	/**
	 * Convert a Direction to the corresponding key code.
	 * 
	 * @param direction
	 *        Direction to process
	 * @return
	 *         Corresponding key code, otherwise <code>null</code>
	 */
	public Integer directionToKeyCode(final Direction direction) {
		switch (direction) {
		case LEFT:
			return KeyEvent.VK_LEFT;
		case RIGHT:
			return KeyEvent.VK_RIGHT;
		case UP:
			return KeyEvent.VK_UP;
		case DOWN:
			return KeyEvent.VK_DOWN;
		default:
			return null;
		}
	}

	/**
	 * Stop the user's movement.
	 */
	public void stopMovement() {
		final RPAction stopAction = new RPAction();

		stopAction.put(TYPE, WALK);
		stopAction.put(MODE, "stop");

		ClientSingletonRepository.getClientFramework().send(stopAction);
	}
}
