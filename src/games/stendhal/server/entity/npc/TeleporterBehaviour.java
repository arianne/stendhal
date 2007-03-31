// $Id$
package games.stendhal.server.entity.npc;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * teleports the SpeakerNPC to a random location on the outside world
 * and causes it to walk a random bit.
 *
 * @author hendrik
 */
public class TeleporterBehaviour implements TurnListener {

	private static Logger logger = Logger.getLogger(TeleporterBehaviour.class);

	private StendhalRPZone zone = null;

	private ArrayList<StendhalRPZone> zones = null;

	private SpeakerNPC speakerNPC = null;

	/**
	 * Creates a new TeleporterBehaviour
	 *
	 * @param speakerNPC SpeakerNPC
	 * @param repeatedText text to repeat
	 */
	public TeleporterBehaviour(final SpeakerNPC speakerNPC, final String repeatedText) {
		this.speakerNPC = speakerNPC;
		listZones();
		TurnNotifier.get().notifyInTurns(60, this, null);
		// say something every minute so that can be noticed more easily
		TurnNotifier.get().notifyInTurns(60, new TurnListener() {

			public void onTurnReached(int currentTurn, String message) {
				speakerNPC.say(repeatedText);
				TurnNotifier.get().notifyInTurns(60 * 3, this, null);
			}
		}, null);
	}

	/**
	 * Creates an ArrayList of "outside" zones for bunny.
	 */
	private void listZones() {
		Iterator itr = StendhalRPWorld.get().iterator();
		zones = new ArrayList<StendhalRPZone>();
		while (itr.hasNext()) {
			StendhalRPZone aZone = (StendhalRPZone) itr.next();
			String zoneName = aZone.getID().getID();
			if (zoneName.startsWith("0") && !zoneName.equals("0_nalwor_city") && !zoneName.equals("0_orril_castle")
			        && !zoneName.equals("0_ados_swamp") && !zoneName.equals("0_ados_outside_w")
			        && !zoneName.equals("0_ados_wall_n")) {
				zones.add(aZone);
			}
		}
	}

	public void onTurnReached(int currentTurn, String message) {
		// Say bye
		speakerNPC.say("Bye.");

		// We make bunny to stop speaking to anyone.
		speakerNPC.setCurrentState(ConversationStates.IDLE);

		// remove bunny from old zone
		zone = speakerNPC.getZone();
		zone.remove(speakerNPC);

		// Teleport to another random place
		boolean found = false;
		int x = -1;
		int y = -1;
		while (!found) {
			zone = zones.get(Rand.rand(zones.size()));
			x = Rand.rand(zone.getWidth() - 4) + 2;
			y = Rand.rand(zone.getHeight() - 5) + 2;
			if (!zone.collides(x, y) && !zone.collides(x, y + 1)) {
				zone.assignRPObjectID(speakerNPC);
				speakerNPC.set(x, y);
				speakerNPC.setDirection(Direction.RIGHT);

				zone.add(speakerNPC);
				StendhalRPRuleProcessor.get().addNPC(speakerNPC);
				found = true;
				logger.debug("Placing bunny at " + zone.getID().getID() + " " + x + " " + y);
			} else {
				logger.info("Cannot place bunny at " + zone.getID().getID() + " " + x + " " + y);
			}
		}

		// try to build a path (but give up after 10 successless tries)
		for (int i = 0; i < 10; i++) {
			int tx = Rand.rand(zone.getWidth() - 4) + 2;
			int ty = Rand.rand(zone.getHeight() - 5) + 2;
			List<Path.Node> path = Path.searchPath(speakerNPC, tx, ty);
			if ((path != null) && (path.size() > 1)) {
				// create path back
				for (int j = path.size() - 1; j > 0; j--) {
					path.add(path.get(j));
				}
				logger.info(path);
				speakerNPC.setPath(path, true);
				break;
			}
		}

		// Schedule so we are notified again in 5 minutes
		TurnNotifier.get().notifyInTurns(5 * 60 * 3, this, null);
	}
}
