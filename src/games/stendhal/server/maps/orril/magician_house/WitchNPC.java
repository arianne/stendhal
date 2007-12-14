package games.stendhal.server.maps.orril.magician_house;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configure Orril Jynath House (Inside/Level 0).
 */
public class WitchNPC implements ZoneConfigurator {
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
		buildJynathHouse(zone, attributes);
	}

	private void buildJynathHouse(StendhalRPZone zone,
			Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Jynath") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(24, 7));
				nodes.add(new Node(21, 7));
				nodes.add(new Node(21, 9));
				nodes.add(new Node(15, 9));
				nodes.add(new Node(15, 12));
				nodes.add(new Node(13, 12));
				nodes.add(new Node(13, 27));
				nodes.add(new Node(22, 27));
				nodes.add(new Node(13, 27));
				nodes.add(new Node(13, 12));
				nodes.add(new Node(15, 12));
				nodes.add(new Node(15, 9));
				nodes.add(new Node(21, 9));
				nodes.add(new Node(21, 7));
				nodes.add(new Node(24, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm a witch, since you ask. I grow #collard as a hobby.");
				addReply("collard",	"That cabbage in the pot. Be careful of it!");
				/*
				 * addHelp("You may want to buy some potions or do some #task
				 * for me.");
				 */
				addHelp("I can #heal you");
				new HealerAdder().addHealer(this, 200);
				addGoodbye();
			}
		};

		npc.setEntityClass("witchnpc");
		npc.setPosition(24, 7);
		npc.initHP(100);
		zone.add(npc);
	}
}
