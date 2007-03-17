package games.stendhal.server.maps.orril;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configure Orril Jynath House (Inside/Level 0).
 */
public class IL0_JynathHouse implements ZoneConfigurator {
	private NPCList npcs;
	

	public IL0_JynathHouse() {
		this.npcs = NPCList.get();
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildJynathHouse(zone, attributes);
	}


	private void buildJynathHouse(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Jynath") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(24, 6));
				nodes.add(new Path.Node(21, 6));
				nodes.add(new Path.Node(21, 8));
				nodes.add(new Path.Node(15, 8));
				nodes.add(new Path.Node(15, 11));
				nodes.add(new Path.Node(13, 11));
				nodes.add(new Path.Node(13, 26));
				nodes.add(new Path.Node(22, 26));
				nodes.add(new Path.Node(13, 26));
				nodes.add(new Path.Node(13, 11));
				nodes.add(new Path.Node(15, 11));
				nodes.add(new Path.Node(15, 8));
				nodes.add(new Path.Node(21, 8));
				nodes.add(new Path.Node(21, 6));
				nodes.add(new Path.Node(24, 6));
			setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm a witch, since you ask.");
				/* addHelp("You may want to buy some potions or do some #task for me."); */
				addHelp("I can #heal you");
				addHealer(200);
				addGoodbye();
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "witchnpc");
		npc.set(24, 6);
		npc.initHP(100);
		zone.add(npc);
	}
}
