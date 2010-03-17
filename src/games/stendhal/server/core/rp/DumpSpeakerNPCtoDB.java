/* $Id$ */
package games.stendhal.server.core.rp;

import games.stendhal.server.core.engine.dbcommand.DumpSpeakerNPCsCommand;
import games.stendhal.server.core.events.TurnListener;
import marauroa.server.db.command.DBCommandQueue;

/**
 * Dumps information of all SpeakerNPCs to the database
 * 
 * @author hendrik
 */
public class DumpSpeakerNPCtoDB implements TurnListener {

	public void onTurnReached(int currentTurn) {
		DBCommandQueue.get().enqueue(new DumpSpeakerNPCsCommand());
	}
}