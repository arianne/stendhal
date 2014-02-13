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
package games.stendhal.server.core.rp;

import games.stendhal.server.core.engine.dbcommand.DumpSpeakerNPCsCommand;
import games.stendhal.server.core.engine.dbcommand.UpdateSearchIndexCommand;
import games.stendhal.server.core.events.TurnListener;
import marauroa.server.db.command.DBCommandQueue;

/**
 * Dumps information of all SpeakerNPCs to the database
 *
 * @author hendrik
 */
public class DumpGameInformationForWebsite implements TurnListener {

	@Override
	public void onTurnReached(int currentTurn) {
		DBCommandQueue.get().enqueue(new DumpSpeakerNPCsCommand());
		DBCommandQueue.get().enqueue(new UpdateSearchIndexCommand());
	}
}