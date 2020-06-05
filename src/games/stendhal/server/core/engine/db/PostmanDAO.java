/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.ChatMessage;
import marauroa.server.db.DBTransaction;

/**
 * Database access for postman messages.
 *
 * @author kymara
 */
public class PostmanDAO {

	private Logger logger = Logger.getLogger(PostmanDAO.class);

	/**
	 * store a message from any named source
	 * We do not specify it must be a player because NPCs use postman to send messages
	 *
	 * @param transaction DBTransaction
	 * @param source  name of source
	 * @param target  name of player that the message is for
	 * @param message 	message to be sent
	 * @param messagetype	N for NPCs, S for support, P for player
	 * @param timestamp timestamp
	 * @throws SQLException in case of an database error
	 */
	public void storeMessage(DBTransaction transaction, String source, String target, String message, String messagetype, Timestamp timestamp) throws SQLException {
		String query = "INSERT INTO postman(source, target, message, messagetype, timedate)"
				+ " values ('[source]', '[target]', '[message]', '[messagetype]', '[timedate]')";
		logger.debug("postman is storing a message " + query);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("target", target);
		params.put("message", message);
		params.put("messagetype", messagetype);
		params.put("timedate", timestamp);
		transaction.execute(query, params);
	}


	/**
	 * gets a list of ChatMessages for this character
	 *
	 * @param transaction DBTransaction
	 * @param charname charname - name of character
	 * @return list of ChatMessages
	 * @throws SQLException in case of an database error
	 */
	public List<ChatMessage> getChatMessages(DBTransaction transaction, String charname) throws SQLException {
		try {
			// we do not yet use the delivered flag but I am being super careful and including the check for it already.
			String query = "SELECT source, message, timedate, messagetype FROM postman WHERE target='[charname]' and delivered = 0 ORDER BY timedate FOR UPDATE";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("charname", charname);
			logger.debug("getChatMessages is executing query " + query);

			ResultSet chatMessagesSet = transaction.query(query, params);
			List<ChatMessage> list = new LinkedList<ChatMessage>();

			while (chatMessagesSet.next()) {
				String source = chatMessagesSet.getString("source");
				String message = chatMessagesSet.getString("message");
				String timedate = chatMessagesSet.getString("timedate");
				String messagetype = chatMessagesSet.getString("messagetype");
				ChatMessage chatmessage = new ChatMessage(source, message, timedate, messagetype);
				list.add(chatmessage);
			}

			chatMessagesSet.close();
			return list;
		} catch (SQLException e) {
			logger.error("Can't query for character \"" + charname + "\"", e);
			throw e;
		}
	}


	/**
	 * marks messages delivered for this character
	 *
	 * @param transaction DBTransaction
	 * @param charname charname - name of character
	 * @throws SQLException in case of an database error
	 */
	public void markMessagesDelivered(DBTransaction transaction, String charname) throws SQLException {
		try {
			String query = "UPDATE postman SET delivered = 1 WHERE target = '[charname]'";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("charname", charname);
			logger.debug("markMessagesDelivered is using query: " + query);

			transaction.execute(query, params);
		} catch (SQLException e) {
			logger.error("Can't mark messages delivered for character \"" + charname + "\"", e);
			throw e;
		}
	}

}
