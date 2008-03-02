package games.stendhal.server.maps.kalavan.citygardens;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds the gardener in Kalavan city gardens.
 *
 * @author kymara
 */
public class GardenerNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Sue") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(100, 123));
				nodes.add(new Node(110, 123));
				nodes.add(new Node(110, 110));
				nodes.add(new Node(119, 110));
				nodes.add(new Node(119, 122));
				nodes.add(new Node(127, 122));
				nodes.add(new Node(127, 111));
				nodes.add(new Node(118, 111));
				nodes.add(new Node(118, 123));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Fine day, isn't it?");
				addReply(ConversationPhrases.YES_MESSAGES, "Very warm...");
				addReply(ConversationPhrases.NO_MESSAGES, "It's better than rain!");
				addJob("I am the gardener. I hope you like the flowerbeds.");
				addHelp("I don't know what you want help with.");
				addOffer("I haven't anything to trade yet. Wait till the autumn when I'll have some cuttings and seeds.");
				addQuest("I'd love a cup of #tea, it's thirsty work, gardening.");
				addReply("tea", "It might be difficult to find, though. The tea shop is closed this week. I'll have to brew my own.");
				addGoodbye("Bye. Enjoy the rest of the gardens.");
			}
		};

		npc.setEntityClass("gardenernpc");
		npc.setPosition(100, 123);
		npc.initHP(100);
		zone.add(npc);
	}
}
