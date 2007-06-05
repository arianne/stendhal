package games.stendhal.server.maps.orril.dungeon;

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
		SpeakerNPC ghost = new SpeakerNPC("Goran") {

		      	//  he has no collisions
			@Override
			public boolean isObstacle(Entity entity) {
			        return false;
			}
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(216, 126));
				nodes.add(new Path.Node(200, 126));
				nodes.add(new Path.Node(200, 119));
				nodes.add(new Path.Node(216, 119));
				nodes.add(new Path.Node(216, 121));
				nodes.add(new Path.Node(200, 121));
				nodes.add(new Path.Node(200, 123));
				nodes.add(new Path.Node(216, 123));
				setPath(nodes, true);
			}

		};
		ghost.setDescription("You see a ghostly figure of a man. He appears to have died in battle.");
		npcs.add(ghost);
		zone.assignRPObjectID(ghost);
		ghost.put("class", "deadmannpc");
		// he is a ghost so he is see through
		ghost.put("visibility",70);
		ghost.set(216, 126);
		// he has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
