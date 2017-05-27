/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.server.core.engine.db.PendingAchievementDAO;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Deletes used details for pending or partially gained achievements from a table
 *
 * @author kymara
 */
public class DeletePendingAchievementDetailsCommand extends AbstractDBCommand {

	private final Player player;

	/**
	 * @param player the player whose achievements should be read
	 */
	public DeletePendingAchievementDetailsCommand(Player player) {
		this.player = player;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		DAORegister.get().get(PendingAchievementDAO.class).deletePendingAchievementDetails(transaction, player.getName());
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "DeletePendingAchievementDetailsCommand [player=" + player.getName() + "]";
	}
}
