package games.stendhal.server.maps.magic.city;

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
 * Builds a Healer NPC for the magic city
 * 
 * @author kymara
 */
public class HealerNPC implements ZoneConfigurator {

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
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Salva Mattori") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				// walks along the aqueduct path, roughly
				nodes.add(new Node(5, 25));
				nodes.add(new Node(5, 51));
				nodes.add(new Node(18, 51));
				nodes.add(new Node(18, 78));
				nodes.add(new Node(20, 78));
				nodes.add(new Node(20, 109));
				// and back again
				nodes.add(new Node(20, 78));
				nodes.add(new Node(18, 78));
				nodes.add(new Node(18, 51));
				nodes.add(new Node(5, 51));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings. Can I #help you?");
				addJob("I practise alchemy and have the ability to #heal others.");
				new HealerAdder().addHealer(this, 500);
				addReply(
						"magical",
						"We're all capable of magic here. There are different kinds, of course. My favourite is the Sunlight Spell to keep grass and flowers growing underground.");
				addHelp("I have #magical powers to #heal your ailments.");
				addQuest("I need nothing, thank you.");
				addGoodbye("Fare thee well.");
			}
		};

		npc.setDescription("You see a quiet woman with a benign face.");
		npc.setEntityClass("cloakedwomannpc");
		npc.setPosition(5, 25);
		npc.initHP(100);
		zone.add(npc);
	}
}
