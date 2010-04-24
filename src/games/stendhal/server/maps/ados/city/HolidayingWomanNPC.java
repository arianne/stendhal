package games.stendhal.server.maps.ados.city;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates a woman NPC to help populate Ados
 *
 */
public class HolidayingWomanNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Alice Farmer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(39, 92));
				nodes.add(new Node(4, 92));
				nodes.add(new Node(4, 63));
				nodes.add(new Node(48, 63));
				nodes.add(new Node(48, 68));
				nodes.add(new Node(39, 68));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello.");
				addHelp("Sorry, I can't help you.");
				addOffer("I can only offer this nice weather today. Its really great.");
				addQuest("I have no task for you, sorry."); 
				addJob("Aaaah, I am on holiday here, only walking around.");
				addGoodbye("Bye bye.");

				}
		};

		npc.setEntityClass("woman_016_npc");
		npc.setPosition(39, 92);
		npc.initHP(100);
		zone.add(npc);
	}
}
