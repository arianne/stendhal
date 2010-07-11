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

import games.stendhal.server.core.engine.db.PostmanDAO;

import java.sql.SQLException;

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
	
	private String npcName;
	private String charName;
	private String message;
	private String messagetype;
	private String accountName;
	
	/**
	 * creates a new StoreMessageCommand
	 *
	 * @param npcName who left the message
	 * @param charName the player name the message is for
	 * @param message what the message is
	 * @param messagetype N for NPCs, S for support, P for player
	 */
	public StoreMessageCommand(String npcName, String charName, String message, String messagetype) {
		this.npcName = npcName;
		this.charName = charName;		
		this.message = message;	
		this.messagetype = messagetype;	
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException {
		CharacterDAO characterdao = DAORegister.get().get(CharacterDAO.class);
		accountName = characterdao.getAccountName(charName);
		if (accountName != null) {
			PostmanDAO postmandao = DAORegister.get().get(PostmanDAO.class);
			postmandao.storeMessage(npcName, charName, message, messagetype);
		}
	}
	
	/**
	 * checks if account name could be found - which tells if the character whom the message was for, existed
	 *
	 * @return true if an account was found for that character name
	 */
	public boolean targetCharacterExists() {
		return accountName != null; 
	}

}
