/***************************************************************************
 *                    (C) Copyright 2014 - Faiumoni e. V.                  *
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
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.db.StendhalSearchIndexDAO;
import games.stendhal.server.core.rp.searchindex.SearchIndexEntry;
import games.stendhal.server.core.rp.searchindex.SearchIndexManager;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * updates the search index
 *
 * @author hendrik
 */
public class UpdateSearchIndexCommand extends AbstractDBCommand {
	private static Logger logger = Logger.getLogger(UpdateSearchIndexCommand.class);

	@Override
	public void execute(DBTransaction transaction) throws SQLException, IOException {
		long start = System.currentTimeMillis();

		SearchIndexManager manager = new SearchIndexManager();
		Set<SearchIndexEntry> index = manager.generateIndex();

		StendhalSearchIndexDAO dao = DAORegister.get().get(StendhalSearchIndexDAO.class);
		dao.updateSearchIndex(transaction, index);

		logger.info("Completed dumping of search index in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

}
