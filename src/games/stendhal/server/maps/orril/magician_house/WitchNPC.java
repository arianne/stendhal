package games.stendhal.server.maps.orril.magician_house;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configure Orril Jynath House (Inside/Level 0).
 */
public class WitchNPC implements ZoneConfigurator {

	private NPCList npcs;

	public WitchNPC() {
		this.npcs = NPCList.get();
	}

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildJynathHouse(zone, attributes);
	}

	private void buildJynathHouse(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Jynath") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(24, 6));
				nodes.add(new Node(21, 6));
				nodes.add(new Node(21, 8));
				nodes.add(new Node(15, 8));
				nodes.add(new Node(15, 11));
				nodes.add(new Node(13, 11));
				nodes.add(new Node(13, 26));
				nodes.add(new Node(22, 26));
				nodes.add(new Node(13, 26));
				nodes.add(new Node(13, 11));
				nodes.add(new Node(15, 11));
				nodes.add(new Node(15, 8));
				nodes.add(new Node(21, 8));
				nodes.add(new Node(21, 6));
				nodes.add(new Node(24, 6));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm a witch, since you ask. I grow #collard as a hobby.");
				addReply("collard","That cabbage in the pot. Be careful of it!");
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
