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
package games.stendhal.server.core.engine.dbcommand;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

public class ReadAchievementsForPlayerCommand extends AbstractDBCommand {

	private Set<String> identifiers = new HashSet<String>();
	private final Player player;

	/**
	 * @param player the player whose achievements should be read
	 */
	public ReadAchievementsForPlayerCommand(Player player) {
		this.player = player;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		identifiers = DAORegister.get().get(AchievementDAO.class).loadAllReachedAchievementsOfPlayer(transaction, getPlayer().getName());
	}

	public Set<String> getIdentifiers() {
		return identifiers;
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "ReadAchievementsForPlayerCommand [player=" + player + "]";
	}
}
