/***************************************************************************
 *                    (C) Copyright 2003-2023 - Stendhal                   *
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

import java.sql.SQLException;

import org.apache.log4j.Logger;

import marauroa.server.db.DBTransaction;

/**
 * database access for dumping information from data/conf into the database.
 */
public class StendhalConfigurationDAO {
	private static Logger logger = Logger.getLogger(StendhalNPCDAO.class);


	/**
	 * dumps all NPCs
	 *
	 * @param transaction DBTransaction
	 * @throws SQLException in case of an database error
	 */
	public void dumpConfiguration(DBTransaction transaction) throws SQLException {
		long start = System.currentTimeMillis();

		// dumpCreatures(transaction);
		// dumpItemClasses(transaction);
		// dumpItems(transaction);
		dumpShops(transaction);
		// dumpZones(transaction);
		logger.debug("Completed dumping of configuration in " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

	void dumpShops(DBTransaction transaction) throws SQLException {
/*
Shop
 name
 type ["buy", "sell", "outfit"]
 
 
Item
 name
 price

Outfit
 name
 layers
 price

 
Merchant
 flag-configure
 flag-noOffer
 
 outfit-flag-removeDetailColor
 outfit-flag-confirmTemp
 outfit-flag-confirmBalloon
 outfit-flag-resetBeforeChange
 
 note
*/
	}
}
