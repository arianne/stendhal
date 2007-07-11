package games.stendhal.server.maps.kotoch;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;

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
 *
 * @author kymara
 */
public class OrcSamanNPC implements ZoneConfigurator {

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
		SpeakerNPC npc = new SpeakerNPC("Orc Saman") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(8, 112));
				nodes.add(new Node(16, 112));
				nodes.add(new Node(16, 114));
				nodes.add(new Node(22, 114));
				nodes.add(new Node(22, 118));				
				nodes.add(new Node(8, 118));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Oof.");
				addJob("Me, Orc Saman.");
				addHelp("Orc Saman need help! Make #task.");
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "No trade.", null);
 				addGoodbye("see yoo.");
			}
		};
		npc.setDescription("You see an Orc Saman.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "orcsamannpc");
		npc.set(8,112);
		npc.initHP(100);
		zone.add(npc);


	}
}
