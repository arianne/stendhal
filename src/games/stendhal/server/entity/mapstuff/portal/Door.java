/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.portal;

import static games.stendhal.common.constants.Actions.MOVE_CONTINUOUS;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * A door is a special kind of portal which can be open or closed.
 *
 * Note that you can link a door with a portal; that way, people only require
 * the key when walking in one direction and can walk in the other direction
 * without any key.
 */
public abstract class Door extends AccessCheckingPortal implements TurnListener {

	/**
	 * How many turns it takes until door automatically closes itself after
	 * somebody walked through it.
	 */
	private static final int SECONDS_TO_STAY_OPEN = 3;

	/**
	 * Whether or not the door is currently open.
	 */
	private boolean open;

	public static void generateRPClass() {
		final RPClass door = new RPClass("door");
		door.isA("entity");
		door.addAttribute("class", Type.STRING);
		door.addAttribute("locked", Type.STRING, Definition.HIDDEN);
		door.addAttribute("open", Type.FLAG);
		door.addAttribute(ATTR_FACE, Type.STRING);
		door.addAttribute(MOVE_CONTINUOUS, Type.FLAG, Definition.VOLATILE);
	}

	/**
	 * Creates a new door.
	 *
	 * @param clazz
	 *            The class. Responsible for how this door looks like.
	 */
	public Door(final String clazz) {
		this(clazz, "This door is closed");
	}

	/**
	 * Creates a new door.
	 *
	 * @param clazz
	 *            The class. Responsible for how this door looks like.
	 *
	 * @param rejectMessage
	 *            The message to given when rejected.
	 */
	public Door(final String clazz, final String rejectMessage) {
		super(rejectMessage);
		setRPClass("door");
		put("type", "door");
		setEntityClass(clazz);

		open = false;
	}

	@Override
	public void update() {
		super.update();
		open = has("open");
	}

	/**
	 * Opens the door.
	 */
	public void open() {
		open = true;
		put("open", "");
		notifyWorldAboutChanges();
	}

	/**
	 * Open door, or stop door from closing
	 */
	private void keepOpen() {
		final TurnNotifier turnNotifier = SingletonRepository.getTurnNotifier();
		if (isOpen()) {
			// The door is still open because another player just used it.
			// Thus, it is scheduled to auto-close soon. We delay this
			// auto-closing.
			turnNotifier.dontNotify(this);
		} else {
			open();
		}
		// register automatic close
		turnNotifier.notifyInSeconds(SECONDS_TO_STAY_OPEN, this);
	}

	/**
	 * Closes the door.
	 */
	protected void close() {
		this.open = false;
		if (has("open")) {
			remove("open");
		}
		notifyWorldAboutChanges();
	}

	/**
	 * Is the door open?
	 *
	 * @return true, if opened; false otherwise
	 */
	protected boolean isOpen() {
		return open;
	}

	/**
	 * Teleport (if the door is now open).
	 */
	@Override
	public boolean onUsed(final RPEntity user) {
		// check first could player use the door
		final boolean couldUse = super.onUsed(user);

		if (couldUse) {
			keepOpen();
		} else {
			// player may not use it
			if (isOpen()) {
				// close now to make visible that the entity is not allowed
				// to pass
				close();
			}
		}

		// finally let player use door
		return couldUse;
	}

	@Override
	public void onUsedBackwards(final RPEntity user, final boolean hadPath) {
		keepOpen();
		notifyWorldAboutChanges();

		// call super method to handle facing direction & continuous movement
		super.onUsedBackwards(user, hadPath);
	}

	@Override
	public String describe() {
		String text = "You see a door.";
		if (hasDescription()) {
			text = getDescription();
		}
		if (isOpen()) {
			text += " It is open.";
		} else {
			text += " It is closed.";
		}

		return (text);
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		close();
		notifyWorldAboutChanges();
	}
}
