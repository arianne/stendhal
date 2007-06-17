package games.stendhal.server.maps.ados.fishermans_hut;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Fisherman (Inside / Level 0)
 *
 * @author dine
 */
public class FishermanNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildGoldsmith(zone, attributes);
	}

	private void buildGoldsmith(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC fisherman = new SpeakerNPC("Pequod") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// from left
				nodes.add(new Path.Node(3, 2));
				// to right
				nodes.add(new Path.Node(12, 2));
				// to left
				nodes.add(new Path.Node(3, 2));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I'm a fisherman.");
				addHelp("Nowadays you can read signposts, books and other things here in Faiumoni.");
				addGoodbye("Goodbye.");
			}
		};
		npcs.add(fisherman);
		zone.assignRPObjectID(fisherman);
		fisherman.put("class", "fishermannpc");
		fisherman.setDirection(Direction.DOWN);
		fisherman.set(3, 2);
		fisherman.initHP(100);
		zone.add(fisherman);
	}
}
