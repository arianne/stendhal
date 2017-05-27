/***************************************************************************
 *                    (C) Copyright 2003-2013 - Marauroa                   *
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

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * checks the state of a quest
 *
 * @author hendrik
 */
public class QuestCheckingPortal extends AccessCheckingPortal {
	private final String questslot;

	private String requiredState;

	/**
	 * creates a quest checking portal
	 *
	 * @param questslot name of quest slot
	 */
	public QuestCheckingPortal(final String questslot) {
		this(questslot, "Why should i go down there?. It looks very dangerous.");
	}

	/**
	 * creates a quest checking portal
	 *
	 * @param questslot name of quest slot
	 * @param rejectMessage message to tell the player, if the condition is not met
	 */
	public QuestCheckingPortal(final String questslot, final String rejectMessage) {
		super(rejectMessage);

		this.questslot = questslot;
	}

	/**
	 * creates a quest checking portal
	 *
	 * @param questslot name of quest slot
	 * @param state expected state
	 * @param rejectMessage message to tell the player, if the condition is not met
	 */
	public QuestCheckingPortal(final String questslot, final String state, final String rejectMessage) {
		super(rejectMessage);
		this.questslot = questslot;
		this.requiredState = state;
	}

	//
	// AccessCheckingPortal
	//

	/**
	 * Determine if this portal can be used.
	 *
	 * @param user
	 *            The user to be checked.
	 *
	 * @return <code>true</code> if the user can use the portal.
	 */
	@Override
	protected boolean isAllowed(final RPEntity user) {
		if (! (user instanceof Player)) {
			return false;
		}

		Player player = (Player) user;
		if (!player.hasQuest(questslot)) {
			return false;
		}

		if (requiredState != null) {
			return (player.isQuestInState(questslot, 0, requiredState));
		}

		return true;
	}
}
