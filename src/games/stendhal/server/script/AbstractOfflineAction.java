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
package games.stendhal.server.script;

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * An abstract super class for actions on offline players
 *
 * @author hendrik
 */
public abstract class AbstractOfflineAction extends ScriptImpl {
	private static Logger logger = Logger.getLogger(AbstractOfflineAction.class);

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		// validate and read parameters
		if (!validateParameters(admin, args)) {
			return;
		}
		String characterName = args.get(0);

		// check that player is offline
		if (StendhalRPRuleProcessor.get().getPlayer(characterName) != null) {
			admin.sendPrivateText("This player is currently online. Please use the normal commands.");
			return;
		}

		// start a transaction
		CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {

			// check that the player exists
			if (!characterDAO.hasCharacter(characterName)) {
				admin.sendPrivateText("No player with that name.");
				TransactionPool.get().commit(transaction);
				return;
			}
			String username = DAORegister.get().get(CharacterDAO.class).getAccountName(transaction, characterName);
			RPObject object = characterDAO.loadCharacter(transaction, username, characterName);

			process(admin, object, args);

			// safe it back
			characterDAO.storeCharacter(transaction, username, characterName, object);
			TransactionPool.get().commit(transaction);

			// remove from world
			IRPZone zone = StendhalRPWorld.get().getRPZone(object.getID());
			if (zone != null) {
				zone.remove(object.getID());
			}

		} catch (Exception e) {
			logger.error(e, e);
			admin.sendPrivateText(e.toString());
			TransactionPool.get().rollback(transaction);
		}
	}

	/**
	 * validates the parameters, sends an error message, if something is wrong with them
	 *
	 * @param admin admin executing the script
	 * @param args arguments for the script
	 * @return true if the parameters are valid, false otherwise
	 */
	public abstract boolean validateParameters(final Player admin, final List<String> args);

	/**
	 * processes the requested operation on the loaded object
	 *
	 * @param admin admin executing the script
	 * @param object the RPObject of the player loaded from the database
	 * @param args arguments for the script
	 */
	public abstract void process(final Player admin, RPObject object, final List<String> args);
}
