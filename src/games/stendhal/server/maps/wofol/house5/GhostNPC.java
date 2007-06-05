package games.stendhal.server.maps.wofol.house5;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds a Ghost NPC
 *
 * @author kymara
 */
public class GhostNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}
	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC ghost = new SpeakerNPC("Zak") {

		      	//  he has no collisions
			@Override
			public boolean isObstacle(Entity entity) {
			        return false;
			}
			@Override
			protected void createPath() {
                                List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(3, 3));
				nodes.add(new Path.Node(10, 3));
				nodes.add(new Path.Node(10, 8));
				nodes.add(new Path.Node(8, 8));
				nodes.add(new Path.Node(8, 6));
				nodes.add(new Path.Node(6, 6));
				nodes.add(new Path.Node(6, 4));
				nodes.add(new Path.Node(3, 4));
				setPath(nodes, true);
			}

		};
		ghost.setDescription("You see a ghostly figure of a man. You have no idea how he died.");
		npcs.add(ghost);
		zone.assignRPObjectID(ghost);
		ghost.put("class", "man_000_npc");
		// he is a ghost so he is see through
		ghost.put("visibility",50);
		ghost.set(3, 3);
		// he has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
