// $Id$
package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import marauroa.common.game.IRPZone;

import org.apache.log4j.Logger;

/**
 * teleports the SpeakerNPC to a random location on the outside world and causes
 * it to walk a random path
 * 
 * @author hendrik
 */
public class TeleporterBehaviour implements TurnListener {

	private static Logger logger = Logger.getLogger(TeleporterBehaviour.class);

	private StendhalRPZone zone;

	private ArrayList<StendhalRPZone> zones;

	private final SpeakerNPC speakerNPC;
	private final String repeatedText;
	
	/**
	 * Creates a new TeleporterBehaviour.
	 * 
	 * @param speakerNPC
	 *            SpeakerNPC
	 * @param repeatedText
	 *            text to repeat
	 */
	public TeleporterBehaviour(final SpeakerNPC speakerNPC,
			final String repeatedText) {
		this.speakerNPC = speakerNPC;
		this.repeatedText = repeatedText;
		listZones();
		SingletonRepository.getTurnNotifier().notifyInTurns(60, this);
		// say something every minute so that can be noticed more easily
		SingletonRepository.getTurnNotifier().notifyInTurns(60, new TurnListener() {

			@Override
			public void onTurnReached(final int currentTurn) {
				doRegularBehaviour();
				SingletonRepository.getTurnNotifier().notifyInTurns(60 * 3, this);
			}
		});
	}

	/**
	 * Creates a new TeleporterBehaviour.
	 * 
	 * @param speakerNPC
	 *            SpeakerNPC
	 * @param repeatedText
	 *            text to repeat
	 * @param useHighProbabilityZones
	 *            true to make teleportation to a hand 
	 *            selected list of zones more likely
	 */
	public TeleporterBehaviour(final SpeakerNPC speakerNPC, final String repeatedText, final boolean useHighProbabilityZones) {
		this(speakerNPC, repeatedText);
		if (useHighProbabilityZones) {
			addHighProbability();
		}
	}

	protected void doRegularBehaviour() {
		speakerNPC.say(repeatedText);
	}
	
	private void addHighProbability() {
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		for (int i = 0; i < 10; i++) {
			zones.add(world.getZone("0_semos_city"));
			zones.add(world.getZone("0_semos_village_w"));
			zones.add(world.getZone("0_semos_plains_n"));
			zones.add(world.getZone("0_semos_plains_ne"));
			zones.add(world.getZone("0_semos_road_e"));
			zones.add(world.getZone("0_semos_plains_s"));
		}
	}

	/**
	 * Creates an ArrayList of "outside" zones for NPC.
	 */
	private void listZones() {
		final Iterator<IRPZone> itr = SingletonRepository.getRPWorld().iterator();
		zones = new ArrayList<StendhalRPZone>();
		final List<String> badZones = new ArrayList<String>();
		// the following are too dangerous
		badZones.add("0_nalwor_city");
		badZones.add("0_orril_castle");
		badZones.add("0_ados_swamp");
		badZones.add("0_ados_outside_w");
		badZones.add("0_ados_wall_n");
		badZones.add("0_fado_forest_se");
		badZones.add("0_fado_forest_s_e2");
		badZones.add("0_semos_mountain_n_w4");
		// the following have historically been very hard to find a path in
		badZones.add("0_ados_city_n");	
		badZones.add("0_ados_ocean_e");
		badZones.add("0_athor_island_w");
		badZones.add("0_nalwor_forest_n");
		badZones.add("0_nalwor_river_s");
		while (itr.hasNext()) {
			final StendhalRPZone aZone = (StendhalRPZone) itr.next();
			final String zoneName = aZone.getName();
			if (zoneName.startsWith("0") && !badZones.contains(zoneName)) {
				zones.add(aZone);
			}
		}
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		if (speakerNPC.getEngine().getCurrentState() != ConversationStates.IDLE) {
			speakerNPC.say("Bye.");
			speakerNPC.setCurrentState(ConversationStates.IDLE);
		}
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
				logger.info("Cannot place teleporting NPC " + speakerNPC.getName() + " at " + zone.getName()
						+ " " + x + " " + y);
			}
		}

		// try to build a path (but give up after 10 failed tries)
		for (int i = 0; i < 10; i++) {
			final int tx = Rand.rand(zone.getWidth() - 4) + 2;
			final int ty = Rand.rand(zone.getHeight() - 5) + 2;
			final List<Node> path = Path.searchPath(speakerNPC, tx, ty);
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
		SingletonRepository.getTurnNotifier().notifyInTurns(5 * 60 * 3, this);
	}
}
