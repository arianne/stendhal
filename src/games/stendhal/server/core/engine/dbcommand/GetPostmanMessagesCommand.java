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
import java.util.List;

import games.stendhal.server.core.engine.ChatMessage;
import games.stendhal.server.core.engine.db.PostmanDAO;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Get postman messages for a player
 *
 * @author kymara
 */
public class GetPostmanMessagesCommand extends AbstractDBCommand {

	private Player player;
	private List<ChatMessage> messages;


	/**
	 * creates a new GetPostmanMessagesCommand
	 *
	 * @param player the player the check originated from
	 */
	public GetPostmanMessagesCommand(Player player) {
		this.player = player;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		PostmanDAO dao = DAORegister.get().get(PostmanDAO.class);
		messages = dao.getChatMessages(transaction, player.getName());
		// mark the messages delivered in the same transaction that we got them in
		dao.markMessagesDelivered(transaction, player.getName());
	}

	/**
	 * gets the list of messages
	 *
	 * @return messages
	 */
	public List<ChatMessage> getMessages() {
		return messages;
	}

	/**
	 * To access the player sending the query
	 *
	 * @return player
	 */
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
		return "GetPostmanMessagesCommand [player=" + player + ", messages="
				+ messages + "]";
	}
}
