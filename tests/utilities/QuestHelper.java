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
package utilities;

import org.junit.BeforeClass;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;
import utilities.RPClass.ItemTestHelper;

/**
 * Helper methods for testing quests.
 *
 * @author hendrik
 */
public abstract class QuestHelper extends PlayerTestHelper  {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
		generatePlayerRPClasses();
		ItemTestHelper.generateRPClasses();
		generateNPCRPClasses();

		MockStendhalRPRuleProcessor.get();
		// load item configurations to handle money and other items
		SingletonRepository.getEntityManager();

		SingletonRepository.getNPCList().clear();
	}

}
