/***************************************************************************
 *                   (C) Copyright 2019 - Stendhal 		                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

public class PlayerPortfolioSlot extends PlayerSlot {

	/**
	 * Creates a new PlayerSlot.
	 *
	 * @param player player
	 */
	public PlayerPortfolioSlot(final String player) {
		super(player);
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		if (!mayAccessPortfolio(entity)) {
			setErrorMessage("Your portfolio is broken. You should look for someone who can fix it.");
			return false;
		}
		return super.isReachableForTakingThingsOutOfBy(entity);
	}

	@Override
	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
		if (!mayAccessPortfolio(entity)) {
			setErrorMessage("Your portfolio is broken. You should look for someone who can fix it.");
			return false;
		}
		return super.isReachableForThrowingThingsIntoBy(entity);
	}

	/**
	 * checks whether the entity may access the portfolio
	 *
	 * @param entity Entity
	 * @return true, if the portfolio may be accessed, false otherwise
	 */
	private boolean mayAccessPortfolio(Entity entity) {
		if (!(entity instanceof Player)) {
			return false;
		}
		Player player = (Player) entity;
		return (player.getFeature("portfolio") != null);
	}
}
