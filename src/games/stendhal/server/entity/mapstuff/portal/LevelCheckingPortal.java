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

import games.stendhal.server.entity.RPEntity;

/**
 * A portal which allows only certain levels of player to use it.
 *
 * @author hendrik
 */
public class LevelCheckingPortal extends AccessCheckingPortal {
	/**
	 * The default maximum level.
	 */
	public static final int DEFAULT_MAX = 9999;

	/**
	 * The default minimum level.
	 */
	public static final int DEFAULT_MIN = 0;

	/**
	 * The minimum level allowed to pass.
	 */
	private final int minLevel;

	/**
	 * The maximum level allowed to pass.
	 */
	private final int maxLevel;

	/**
	 * Creates a level checking portal.
	 *
	 * @param minLevel
	 *            The minimum level allowed to pass.
	 * @param maxLevel
	 *            The maximum level allowed to pass.
	 */
	public LevelCheckingPortal(final int minLevel, final int maxLevel) {
		this(minLevel, maxLevel, null);
	}

	/**
	 * Creates a level checking portal.
	 *
	 * @param minLevel
	 *            The minimum level allowed to pass.
	 * @param maxLevel
	 *            The maximum level allowed to pass.
	 * @param rejectMessage
	 *            The custom rejection message.
	 */
	public LevelCheckingPortal(final int minLevel, final int maxLevel, final String rejectMessage) {
		super(rejectMessage);

		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
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
		final int level = user.getLevel();

		if ((level < minLevel) || (level > maxLevel)) {
			return false;
		}

		return true;

	}

	/**
	 * Called when the user is rejected. This sends a rejection message to the
	 * user.
	 *
	 * @param user
	 *            The rejected user.
	 */
	@Override
	protected void rejected(final RPEntity user) {
		if (rejectedMessage != null) {
			super.rejected(user);
		} else if (user.getLevel() < minLevel) {
			sendMessage(user, "I am too weak to use this portal.");
		} else if (user.getLevel() > maxLevel) {
			sendMessage(user, "I am too strong to use this portal.");
		}
	}
}
