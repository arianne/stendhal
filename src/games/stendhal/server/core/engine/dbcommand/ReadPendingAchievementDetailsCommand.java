/* $Id$ */
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
import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.engine.db.PendingAchievementDAO;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Reads details for pending or partially gained achievements from a table
 *
 * @author kymara
 */
public class ReadPendingAchievementDetailsCommand extends AbstractDBCommand {

	private final Player player;
	private Map<String, Map<String, Integer>> details = new HashMap<String, Map<String, Integer>>();

	/**
	 * @param player the player whose achievements should be read
	 */
	public ReadPendingAchievementDetailsCommand(Player player) {
		this.player = player;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		details = DAORegister.get().get(PendingAchievementDAO.class).getPendingAchievementDetails(transaction, getPlayer().getName());
	}

	public Map<String, Map<String, Integer>> getDetails() {
		return details;
	}

	public Map<String, Integer> getDetails(String identifier) {
		return details.get(identifier);
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
		return "ReadPendingAchievementDetailsCommand [player=" + player.getName() + "]";
	}
}
