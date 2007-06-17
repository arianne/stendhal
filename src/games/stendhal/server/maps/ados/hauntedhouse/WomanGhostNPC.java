package games.stendhal.server.maps.ados.hauntedhouse;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds a female Ghost NPC
 *
 * @author kymara
 */
public class WomanGhostNPC implements ZoneConfigurator {

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
		SpeakerNPC woman = new SpeakerNPC("Carena") {

		      	//  she has no collisions
			@Override
			public boolean isObstacle(Entity entity) {
			        return false;
			}
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(3, 3));
				nodes.add(new Path.Node(16, 3));
				nodes.add(new Path.Node(16, 13));
				nodes.add(new Path.Node(3, 13));
				nodes.add(new Path.Node(3, 25));
				nodes.add(new Path.Node(11, 25));
				nodes.add(new Path.Node(11, 6));
				nodes.add(new Path.Node(23, 6));
				nodes.add(new Path.Node(23, 28));
				nodes.add(new Path.Node(29, 28));
				nodes.add(new Path.Node(29, 1));
				nodes.add(new Path.Node(21, 1));
				nodes.add(new Path.Node(21, 6));
				nodes.add(new Path.Node(3, 6));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Wooouhhhhhh!");
				addJob("I can do nothing useful on this earthly world. I haunt this house now.");
				addHelp("Here is a warning: if you die, you will become a ghost like me, partially visible and intangible. But if you can find your way out of the afterlife, you will be reborn.");
				addGoodbye("Bye");
				// To do: add interesting things
			}
		};
		woman.setDescription("You see a ghostly figure of a woman. She appears somehow sad.");
		npcs.add(woman);
		zone.assignRPObjectID(woman);
		woman.put("class", "woman_011_npc");
		// She is a ghost so she is see through
		woman.put("visibility",40);
		woman.set(3, 3);
		// She has low HP
		woman.initHP(30);
		zone.add(woman);
	}
}
