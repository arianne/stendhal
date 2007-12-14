// $Id$
package games.stendhal.server.entity.npc;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;
import games.stendhal.server.pathfinder.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import marauroa.common.game.IRPZone;

/**
 * teleports the SpeakerNPC to a random location on the outside world and causes
 * it to walk a random bit.
 * 
 * @author hendrik
 */
public class TeleporterBehaviour implements TurnListener {

	private static Logger logger = Logger.getLogger(TeleporterBehaviour.class);

	private StendhalRPZone zone;

	private ArrayList<StendhalRPZone> zones;

	private SpeakerNPC speakerNPC;

	/**
	 * Creates a new TeleporterBehaviour
	 * 
	 * @param speakerNPC
	 *            SpeakerNPC
	 * @param repeatedText
	 *            text to repeat
	 */
	public TeleporterBehaviour(final SpeakerNPC speakerNPC,
			final String repeatedText) {
		this.speakerNPC = speakerNPC;
		listZones();
		TurnNotifier.get().notifyInTurns(60, this);
		// say something every minute so that can be noticed more easily
		TurnNotifier.get().notifyInTurns(60, new TurnListener() {

			public void onTurnReached(int currentTurn) {
				speakerNPC.say(repeatedText);
				TurnNotifier.get().notifyInTurns(60 * 3, this);
			}
		});
	}

	/**
	 * Creates an ArrayList of "outside" zones for NPC
	 */
	private void listZones() {
		Iterator<IRPZone> itr = StendhalRPWorld.get().iterator();
		zones = new ArrayList<StendhalRPZone>();
		List<String> badZones = new ArrayList<String>();
		badZones.add("0_nalwor_city");
		badZones.add("0_orril_castle");
		badZones.add("0_ados_swamp");
		badZones.add("0_ados_outside_w");
		badZones.add("0_ados_wall_n");
		while (itr.hasNext()) {
			StendhalRPZone aZone = (StendhalRPZone) itr.next();
			String zoneName = aZone.getName();
			if (zoneName.startsWith("0") && !badZones.contains(zoneName)) {
				zones.add(aZone);
			}
		}
	}

	public void onTurnReached(int currentTurn) {
		// Say bye
		speakerNPC.say("Bye.");

		// We make NPC to stop speaking to anyone.
		speakerNPC.setCurrentState(ConversationStates.IDLE);

		// remove NPC from old zone
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
				speakerNPC.setPosition(x, y);
				speakerNPC.setDirection(Direction.RIGHT);

				zone.add(speakerNPC);
				found = true;
				logger.debug("Placing teleporting NPC at " + zone.getName()
						+ " " + x + " " + y);
			} else {
				logger.info("Cannot place teleporting NPC at " + zone.getName()
						+ " " + x + " " + y);
			}
		}

		// try to build a path (but give up after 10 successless tries)
		for (int i = 0; i < 10; i++) {
			int tx = Rand.rand(zone.getWidth() - 4) + 2;
			int ty = Rand.rand(zone.getHeight() - 5) + 2;
			List<Node> path = Path.searchPath(speakerNPC, tx, ty);
			if ((path != null) && (path.size() > 1)) {
				// create path back
				for (int j = path.size() - 1; j > 0; j--) {
					path.add(path.get(j));
				}
				logger.debug(path);
				speakerNPC.setPath(new FixedPath(path, true));
				break;
			}
		}

		// Schedule so we are notified again in 5 minutes
		TurnNotifier.get().notifyInTurns(5 * 60 * 3, this);
	}
}
