/***************************************************************************
 *                    (C) Copyright 2007-2010 - Stendhal                   *
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

import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Checks if a character exists
 *
 * @author kymara
 */
public class CheckCharacterExistsCommand extends AbstractDBCommand {
	private String who;
	private Player player;
	private String accountName;

	/**
	 * creates a new CheckCharacterExistsCommand
	 *
	 * @param player the player the check originated from
	 * @param who the character name to check
	 */
	public CheckCharacterExistsCommand(Player player, String who) {
		this.player = player;
		this.who = who;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		CharacterDAO dao = DAORegister.get().get(CharacterDAO.class);
		accountName = dao.getAccountName(who);
	}

	/**
	 * checks if account name could be found - which tells if the character existed
	 *
	 * @return true if an account was found for that character name
	 */
	public boolean exists() {
		return accountName != null; 
	}
	
	/**
	 * To access the character name we queried
	 *
	 * @return who the character who we checked for
	 */
	public String getWho() {
		return who;
	}
	
	/**
	 * To access the player sending the query
	 *
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}
}
