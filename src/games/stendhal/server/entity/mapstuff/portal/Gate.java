/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.PuzzleEntity;
import games.stendhal.server.entity.mapstuff.puzzle.PuzzleBuildingBlock;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.AlwaysTrueCondition;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

public class Gate extends Entity implements UseListener, TurnListener, PuzzleEntity {
	private static final String HORIZONTAL = "h";
	private static final String VERTICAL = "v";
	private static final String ORIENTATION = "orientation";
	private static final String IMAGE = "image";
	private static final String GATE_ID = "identifier";
	private static final String DEFAULT_IMAGE = "fence_gate";

	private PuzzleBuildingBlock puzzleBuildingBlock;

	public static void generateGateRPClass() {
		if (!RPClass.hasRPClass("gate")) {
			final RPClass gate = new RPClass("gate");
			gate.isA("entity");
			gate.addAttribute(ORIENTATION, Type.STRING);
			gate.addAttribute(IMAGE, Type.STRING);
			gate.addAttribute(GATE_ID, Type.STRING);
		}
	}

	/** Current state of the gate. */
	private boolean isOpen;

	/** Condition for allowing use of the gate. */
	private final ChatCondition condition;

	/**
	 * Time the door should keep open before closing. 0 if it should
	 * not close automatically.
	 */
	private int autoCloseDelay;

	/** Message send to a player trying to open a locked gate. */
	private String refuseMessage;

	/**
	 * Create a new gate.
	 *
	 * @param orientation gate orientation. Either "v" or "h".
	 * @param image image used for the gate
	 * @param condition conditions required for opening the gate, or <code>null</code>
	 * 	if no checking is required
	 */
	public Gate(final String orientation, String image, ChatCondition condition) {
		setRPClass("gate");
		put("type", "gate");
		put(GATE_ID, "");

		setOrientation(orientation);
		setOpen(false);

		if (condition == null) {
			condition = new AlwaysTrueCondition();
		}
		this.condition = condition;

		if (image != null) {
			put(IMAGE, image);
		} else {
			put(IMAGE, DEFAULT_IMAGE);
		}
	}

	/**
	 * Create a new vertical gate.
	 */
	public Gate() {
		this(VERTICAL, null, null);
	}

	/**
	 * Set the orientation of the gate.
	 *
	 * @param orientation "h" for horizontal, "v" for vertical
	 */
	private void setOrientation(final String orientation) {
		if (HORIZONTAL.equals(orientation)) {
			put(ORIENTATION, HORIZONTAL);
		} else {
			put(ORIENTATION, VERTICAL);
		}
	}

	/**
	 * Open the gate.
	 */
	public void open() {
		setOpen(true);
	}

	/**
	 * Check if the gate is open.
	 *
	 * @return true iff the gate is open
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Close the gate.
	 */
	public void close() {
		setOpen(false);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (this.nextTo(user)) {
			if (isAllowed(user)) {
				setOpen(!isOpen());
				return true;
			} else if (refuseMessage != null) {
				user.sendPrivateText(refuseMessage);
			}
		}
		return false;
	}

	/**
	 * Make the gate close automatically after specified delay
	 * once it's been opened.
	 *
	 * @param seconds time to keep the gate open
	 */
	public void setAutoCloseDelay(int seconds) {
		autoCloseDelay = seconds;
	}

	/**
	 * Check if a player can use the gate.
	 *
	 * @param user player trying to close or open the gate
	 * @return <code>true</code> iff the player is allowed to use the gate
	 */
	protected boolean isAllowed(final RPEntity user) {
		Sentence sentence = ConversationParser.parse(user.get("text"));
		return condition.fire((Player) user, sentence, this);
	}

	/**
	 * Set the door open or closed.
	 *
	 * @param open true if the door is opened, false otherwise
	 */
	protected void setOpen(final boolean open) {
		final TurnNotifier turnNotifier = SingletonRepository.getTurnNotifier();

		if (open) {
			setResistance(0);
			if (autoCloseDelay != 0) {
				turnNotifier.notifyInSeconds(autoCloseDelay, this);
			}
		} else {
			// Closing the gate - check there's nobody on the way
			if (getZone() != null)  {
				for (Entity entity : getZone().getEntitiesAt(getX(), getY())) {
					if (entity.getResistance() > 0) {
						return;
					}
				}
			}
			setResistance(100);
			// Stop the notifier, so that the door does not slam in front
			// of someone who just opened it
			turnNotifier.dontNotify(this);
		}
		isOpen = open;
		notifyWorldAboutChanges();
	}

	/**
	 * Get the identifier of the gate
	 *
	 * @return the gate's identifier
	 */
	public String getIdentifier() {
		if (has("id")) {
			return get(GATE_ID);
		} else {
			return null;
		}
	}

	/**
	 * Sets this gate's identifier
	 *
	 * @param id the new identifier of the gate
	 */
	public void setIdentifier(String id) {
		if (id != null) {
			put(GATE_ID, id);
		}
	}

	/**
	 * Set the message to be send to the player if she's not allowed to open
	 * the gate
	 *
	 * @param message
	 */
	public void setRefuseMessage(String message) {
		refuseMessage = message;
	}

	/**
	 * Retrieve message to be sent when access is refused.
	 */
	public String getRefuseMessage() {
		return refuseMessage;
	}

	/**
	 * Callback for the turn notifier to automatically close the gate if the
	 * interval is set
	 */
	@Override
	public void onTurnReached(int currentTurn) {
		setOpen(false);
		/*
		 * If something was in the way, the closing failed.
		 * Try again after the usual delay.
		 */
		if (isOpen) {
			final TurnNotifier turnNotifier = SingletonRepository.getTurnNotifier();
			turnNotifier.notifyInSeconds(autoCloseDelay, this);
		}
	}

	@Override
	public void puzzleExpressionsUpdated() {
		setOpen(puzzleBuildingBlock.get("active", Boolean.class).booleanValue());
	}

	@Override
	public void setPuzzleBuildingBlock(PuzzleBuildingBlock buildingBlock) {
		puzzleBuildingBlock = buildingBlock;
		buildingBlock.put("active", isOpen);
		buildingBlock.put("enabled", true);
	}
}
