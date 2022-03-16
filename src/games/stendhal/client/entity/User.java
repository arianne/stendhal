/***************************************************************************
 *                (C) Copyright 2003-2022 - Faiumoni e.V.                  *
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameObjects;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.common.grammar.Grammar;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This class identifies the user of this client.
 *
 * @author durkham, hendrik
 */
public class User extends Player {
	private static final StaticUserProxy NO_USER = new NoUserProxy();
	private static final String IGNORE_SLOT = "!ignore";

	private static String groupLootmode;
	private static Set<String> groupMembers = Collections.emptySet();
	private static StaticUserProxy userProxy = NO_USER;

	private final Set<String> ignore = new HashSet<String>();
	private final SpeedPredictor speedPredictor;

	/**
	 * creates a User object
	 */
	public User() {
		if (isNull()) {
			speedPredictor = new SpeedPredictor();
		} else {
			speedPredictor = new SpeedPredictor(userProxy.getUser().speedPredictor);
		}
		userProxy = new NormalUserProxy(this);
	}

	/**
	 * gets the User object
	 *
	 * @return user object
	 */
	public static User get() {
		return userProxy.getUser();
	}

	/**
	 * is the user object not set, yet?
	 *
	 * @return true, if the the user object is unknown; false if it is known
	 */
	public static boolean isNull() {
		return userProxy == NO_USER;
	}

	/**
	 * Resets the class to uninitialized.
	 */
	static void setNull() {
		userProxy = NO_USER;
	}

	/**
	 * gets the name of the player's character
	 *
	 * @return charname or <code>null</code>
	 */
	public static String getCharacterName() {
		return userProxy.getName();
	}

	/**
	 * gets the level of the current user
	 *
	 * @return level
	 */
	public static int getPlayerLevel() {
		return userProxy.getPlayerLevel();
	}

	/**
	 * gets the server release version
	 *
	 * @return server release version or <code>null</code>
	 */
	public static String getServerRelease() {
		return userProxy.getServerRelease();
	}

	/**
	 * is the specified charname a buddy of us?
	 *
	 * @param name charname to test
	 * @return true, if it is a buddy, false if it is not a buddy or the user object is unknown.
	 */
	public static boolean hasBuddy(String name) {
		return userProxy.hasBuddy(name);
	}

	/**
	 * is this user an admin with an adminlevel equal or above 600?
	 *
	 * @return true, if the user is an admin; false otherwise
	 */
	public static boolean isAdmin() {
		return userProxy.isAdmin();
	}

	/**
	 * is the player in a group which shares the loot?
	 *
	 * @return true if this player is a group and it uses shared looting
	 */
	public static boolean isGroupSharingLoot() {
		return "shared".equals(groupLootmode);
	}

	/**
	 * is the named player ignored?
	 *
	 * @param name name of player
	 * @return true, if the player should be ignored; false otherwise
	 */
	public static boolean isIgnoring(String name) {
		return userProxy.isIgnoring(name);
	}

	/**
	 * checks if the specified player is in the same group as this player
	 *
	 * @param otherPlayer name of the other player
	 * @return true if the other player is in the same group
	 */
	public static boolean isPlayerInGroup(String otherPlayer) {
		return groupMembers.contains(otherPlayer);
	}

	/**
	 * updates the group information
	 *
	 * @param members members
	 * @param lootmode lootmode
	 */
	public static void updateGroupStatus(Collection<String> members, String lootmode) {
		Set<String> oldGroupMembers = groupMembers;

		if (members == null) {
			groupMembers = Collections.emptySet();
		} else {
			groupMembers = new HashSet<>(members);
		}
		groupLootmode = lootmode;

		// fire change event to color of player object on minimap
		for (IEntity entity : GameObjects.getInstance()) {
			if ((entity instanceof Player)
					&& (oldGroupMembers.contains(entity.getName())
							|| groupMembers.contains(entity.getName()))) {
				((Player) entity).fireChange(RPEntity.PROP_GROUP_MEMBERSHIP);
			}
		}
	}

	/**
	 * calculates the squared distance between the user and the specified coordinates
	 *
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the squared distance
	 */
	static double squaredDistanceTo(final double x, final double y) {
		return userProxy.squareDistanceTo(x, y);
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
				return item.getID().getObjectID();
			}
		}

		return -1;
	}

	/**
	 * checks whether the user owns a pet
	 *
	 * @return true, if the user owns a pet; false otherwise
	 */
	public boolean hasPet() {
		return rpObject.has("pet");
	}

	/**
	 * gets the ID of a pet
	 *
	 * @return ID of pet
	 */
	public int getPetID() {
		return rpObject.getInt("pet");
	}

	/**
	 * checks whether the user owns a sheep
	 *
	 * @return true, if the user owns a sheep; false otherwise
	 */
	public boolean hasSheep() {
		return rpObject.has("sheep");
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
	 * gets the zone name
	 *
	 * @return zone name
	 */
	public String getZoneName() {
		return getID().getZoneID();
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

	@Override
	protected void onAway(final String message) {
		super.onAway(message);

		String text;
		if (message == null) {
			text = "You are no longer marked as being away.";
		} else {
			text = "You have been marked as being away.";
		}
		notifyUser(text, NotificationType.INFORMATION);
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
		/* TODO: enable when walking bug fix is finished. */
		/*
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
				// Stop the character's movement.
				this.stopMovement();

				if (logger.isDebugEnabled() || Testing.DEBUG) {
					logger.info(debugString);
				}
			}
		}
		*/

		super.onChangedAdded(object, changes);

		// The first time we ignore it.
		if (object != null) {
			notifyUserAboutPlayerOnlineChanges(changes);

			if (changes.hasSlot(IGNORE_SLOT)) {
				RPObject ign = changes.getSlot(IGNORE_SLOT).getFirst();
				if (ign != null) {
					addIgnore(ign);
				}
			}
		}
	}

	@Override
	public void onChangedRemoved(final RPObject base, final RPObject diff) {
		super.onChangedRemoved(base, diff);
		if (diff.hasSlot(IGNORE_SLOT)) {
			RPObject ign = diff.getSlot(IGNORE_SLOT).getFirst();
			if (ign != null) {
				removeIgnore(ign);
			}
		}
	}

	@Override
	public void onHealed(final int amount) {
		super.onHealed(amount);
		String pointDesc = Grammar.quantityplnoun(amount, "health point");
		notifyUser(getTitle() + " heals " + pointDesc + ".", NotificationType.HEAL);
	}

	private void notifyUser(String message, NotificationType type) {
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(message, type));
	}

	private void notifyUserAboutPlayerOnlineChanges(RPObject changes) {
		notifyUserAboutPlayerStatus(changes, "offline", " has left Stendhal.");
		notifyUserAboutPlayerStatus(changes, "online", " has joined Stendhal.");
	}

	private void notifyUserAboutPlayerStatus(RPObject changes, String status, String messageEnd) {
		if (changes.has(status)) {
			String[] players = changes.get(status).split(",");
			for (String playername : players) {
				notifyUser(playername + messageEnd, NotificationType.INFORMATION);
			}
		}
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

	@Override
	protected void processPositioning(final RPObject base, final RPObject diff) {
		if (speedPredictor.isActive() && (diff.has("direction") || diff.has("x") || diff.has("y"))) {
			speedPredictor.onMoved();
		}
		super.processPositioning(base, diff);
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

	/**
	 * Interface to separate the no user special case from the normal
	 * situation.
	 */
	private static interface StaticUserProxy {
		String getName();
		int getPlayerLevel();
		String getServerRelease();
		User getUser();
		boolean hasBuddy(String buddy);
		boolean isAdmin();
		boolean isIgnoring(String name);
		double squareDistanceTo(double x, double y);
	}

	private static class NormalUserProxy implements StaticUserProxy {
		private final User user;

		NormalUserProxy(User user) {
			this.user = user;
		}

		@Override
		public String getName() {
			return user.getName();
		}

		@Override
		public int getPlayerLevel() {
			return user.getLevel();
		}

		@Override
		public String getServerRelease() {
			return user.rpObject.get("release");
		}

		@Override
		public User getUser() {
			return user;
		}

		@Override
		public boolean hasBuddy(String buddy) {
			return user.rpObject.has("buddies", buddy);
		}

		@Override
		public boolean isAdmin() {
			return ((user.rpObject != null)
					&& user.rpObject.has("adminlevel")
					&& (user.rpObject.getInt("adminlevel") >= 600));
		}

		@Override
		public double squareDistanceTo(double x, double y) {
			double xDiff = user.getX() - x;
			double yDiff = user.getY() - y;
			return xDiff * xDiff + yDiff * yDiff;
		}

		@Override
		public boolean isIgnoring(String name) {
			return user.ignore.contains(name);
		}
	}

	private static class NoUserProxy implements StaticUserProxy {
		@Override
		public String getName() {
			return null;
		}

		@Override
		public int getPlayerLevel() {
			return 0;
		}

		@Override
		public String getServerRelease() {
			return null;
		}

		@Override
		public User getUser() {
			return null;
		}

		@Override
		public boolean hasBuddy(String buddy) {
			return false;
		}

		@Override
		public boolean isAdmin() {
			return false;
		}

		@Override
		public boolean isIgnoring(String name) {
			return false;
		}

		@Override
		public double squareDistanceTo(double x, double y) {
			return Double.POSITIVE_INFINITY;
		}
	}
}
