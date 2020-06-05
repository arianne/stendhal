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

import java.sql.SQLException;

import games.stendhal.server.core.engine.db.PostmanDAO;
import games.stendhal.server.core.engine.db.StendhalBuddyDAO;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Store postman messages for a player, if a character for them exists
 * Can find out from this function if the character existed
 *
 * @author kymara
 */
public class StoreMessageCommand extends AbstractDBCommand {

	private final String source;
	private final String target;
	private final String message;
	private final String messagetype;
	private String accountName;
	private boolean ignored = false;

	/**
	 * creates a new StoreMessageCommand
	 *
	 * @param source who left the message
	 * @param target the player name the message is for
	 * @param message what the message is
	 * @param messagetype N for NPCs, S for support, P for player
	 */
	public StoreMessageCommand(String source, String target, String message, String messagetype) {
		this.source = source;
		this.target = target;
		this.message = message;
		this.messagetype = messagetype;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);
		accountName = characterDAO.getAccountName(transaction, target);
		if (accountName == null) {
			return;
		}

		if (messagetype.equals("P")) {
			StendhalBuddyDAO buddyDAO = DAORegister.get().get(StendhalBuddyDAO.class);
			if (buddyDAO.isIgnored(transaction, target, source)) {
				ignored = true;
				return;
			}
		}

		PostmanDAO postmanDAO = DAORegister.get().get(PostmanDAO.class);
		postmanDAO.storeMessage(transaction, source, target, message, messagetype, getEnqueueTime());
	}

	/**
	 * checks if account name could be found - which tells if the character whom the message was for, existed
	 *
	 * @return true if an account was found for that character name
	 */
	public boolean targetCharacterExists() {
		return accountName != null;
	}

	/**
	 * is ignored
	 *
	 * @return ignored
	 */
	public boolean isIgnored() {
		return ignored;
	}

	/**
	 * To access the character name we queried
	 *
	 * @return target the character who we checked for
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * To access the source message sender
	 *
	 * @return source who sent the message
	 */
	public String getSource() {
		return source;
	}

	/**
	 * To access the message
	 *
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return "StoreMessageCommand [source=" + source + ", target=" + target
				+ ", message=" + message + ", messagetype=" + messagetype
				+ ", accountName=" + accountName + "]";
	}
}
