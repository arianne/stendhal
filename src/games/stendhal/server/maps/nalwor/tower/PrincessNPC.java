package games.stendhal.server.maps.nalwor.tower;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Princess NPC who lives in a tower
 *
 * @author kymara
 */
public class PrincessNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Tywysoga") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(17, 13));
				nodes.add(new Node(10, 13));
				nodes.add(new Node(10, 4));
				nodes.add(new Node(3, 4));
				nodes.add(new Node(3, 3));
				nodes.add(new Node(7, 3));
				nodes.add(new Node(7, 9));
				nodes.add(new Node(12, 9));
				nodes.add(new Node(12, 13));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hail to thee, human.");
				addJob("I'm a princess. What can I do?");
				addHelp("A persistent person could do a #task for me.");
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "I don't trade. My parents would have considered it beneath me.", null);
 				addGoodbye("Goodbye, strange one.");
			}
		};
		npc.setDescription("You see a beautiful but forlorn High Elf.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "elfprincessnpc");
		npc.set(17, 13);
		npc.initHP(100);
		zone.add(npc);
	}
}
