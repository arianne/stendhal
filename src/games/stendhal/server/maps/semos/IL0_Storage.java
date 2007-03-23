package games.stendhal.server.maps.semos;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

public class IL0_Storage implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosStorageArea(zone, attributes);
	}

	private void buildSemosStorageArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Eonna") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(4, 12)); // its around the table with
				// the beers and to the
				// furnance
				nodes.add(new Path.Node(15, 12));
				nodes.add(new Path.Node(15, 12));
				nodes.add(new Path.Node(15, 8));
				nodes.add(new Path.Node(10, 8));
				nodes.add(new Path.Node(10, 12));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi there, young hero.");
				addJob("I'm just a regular housewife.");
				addHelp("I don't think I can help you with anything.");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "welcomernpc");
		npc.set(4, 12);
		npc.initHP(100);
		zone.add(npc);
		npc = new SpeakerNPC("Ketteh Wehoh") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(21, 5));
				nodes.add(new Path.Node(29, 5));
				nodes.add(new Path.Node(29, 9));
				nodes.add(new Path.Node(21, 9));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addHelp("I am the town Decency and Manners Warden. I can advise you on how to conduct yourself in many ways; like not wandering around naked, for instance.");
				addJob("My job is to maintain a civilized level of behaviour in Semos. I know the protocol for every situation, AND all the ways of handling it wrong. Well, sometimes I get confused on whether to use a spoon or a fork; but then, nobody really uses cutlery in Semos anyway.");
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "The only task I have for you is to behave nicely towards others.", null);
				addGoodbye();
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "elegantladynpc");
		npc.set(21, 5);
		npc.initHP(100);
		zone.add(npc);
	}
}
