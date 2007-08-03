package games.stendhal.server.maps.nalwor.tower;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

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
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(17, 12));
				nodes.add(new Path.Node(10, 12));
				nodes.add(new Path.Node(10, 3));
				nodes.add(new Path.Node(3, 3));
				nodes.add(new Path.Node(3, 2));
				nodes.add(new Path.Node(7, 2));
				nodes.add(new Path.Node(7, 8));
				nodes.add(new Path.Node(12, 8));
				nodes.add(new Path.Node(12, 12));
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
		npc.set(17, 12);
		npc.initHP(100);
		zone.add(npc);

	}
}
