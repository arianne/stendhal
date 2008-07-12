package games.stendhal.server.maps.semos.storage;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DecencyAndMannersWardenNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSemosStorageArea(zone, attributes);
	}

	private void buildSemosStorageArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Ketteh Wehoh") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(21, 6));
				nodes.add(new Node(29, 6));
				nodes.add(new Node(29, 10));
				nodes.add(new Node(21, 10));
				setPath(new FixedPath(nodes, true));
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

		npc.setEntityClass("elegantladynpc");
		npc.setPosition(21, 6);
		npc.initHP(100);
		zone.add(npc);
	}
}
