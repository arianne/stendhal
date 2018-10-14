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
package games.stendhal.tools.playerUpdate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.db.CharacterIterator;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.UpdateConverter;
import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.DatabaseFactory;

/**
 * Loads all Players from the database, performs update operations and saves afterwards.
 * @author madmetzger
 */
public class UpdatePlayerEntities {
	private static Logger logger = Logger.getLogger(UpdatePlayerEntities.class);

    /**
     * Inits all RPClasses, has to be called before doing update. Split off due to testing issues.
     */
	public void initRPClasses() {
		StendhalRPWorld.get();
	}

	private void loadAndUpdatePlayers(DBTransaction transaction) throws SQLException, IOException {
    	final Iterator<RPObject> i = new CharacterIterator(transaction, true);
    	while (i.hasNext()) {
    		final RPObject next = i.next();
    		System.out.println(next);
    		final Player p = createPlayerFromRPO(next);
    		savePlayer(transaction, p);
    	}
    }

	Player createPlayerFromRPO(final RPObject next) {
		UpdateConverter.updatePlayerRPObject(next);
		final Player p = new Player(next);
		UpdateConverter.updateQuests(p);
		return p;
	}

	void savePlayer(DBTransaction transaction, final Player player) throws SQLException, IOException {
		DAORegister.get().get(CharacterDAO.class).storeCharacter(transaction, player.getName(), player.getName(), player);
	}

    private void doUpdate() {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			this.loadAndUpdatePlayers(transaction);
			TransactionPool.get().commit(transaction);
		} catch (Exception e) {
			logger.error(e, e);
			TransactionPool.get().rollback(transaction);
		}
	}

	public static void main(final String[] args) {
		new DatabaseFactory().initializeDatabase();
		UpdatePlayerEntities updatePlayerEntities = new UpdatePlayerEntities();
        updatePlayerEntities.initRPClasses();
		updatePlayerEntities.doUpdate();
    }
}
