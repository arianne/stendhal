/* $Id$ */
package games.stendhal.server.core.rp;

import games.stendhal.server.core.engine.db.StendhalNPCDAO;
import games.stendhal.server.core.events.TurnListener;
import marauroa.server.game.db.DAORegister;

/**
 * Dumps information of all SpeakerNPCs to the database
 * 
 * @author hendrik
 */
public class DumpSpeakerNPCtoDB implements TurnListener {

	public void onTurnReached(int currentTurn) {
		DAORegister.get().get(StendhalNPCDAO.class).dumpNPCs();
	}
}