package games.stendhal.server.maps.ados.wall;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates a boy NPC to help populate Ados
 *
 */
public class HolidayingBoyNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Finn Farmer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(114, 77));
				nodes.add(new Node(98, 77));
				nodes.add(new Node(98, 69));
				nodes.add(new Node(116, 69));
				nodes.add(new Node(116, 77));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey.");
				addHelp("No, you can't help me.");
				addOffer("Ooooh, have you seen the lovely cats from Felina? I hope my parents " +
						"will buy one for me. Would be a great holiday gift :-)");
				addQuest("Task? No, not from me."); 
				addJob("Hey!! I am a little boy!");
				addGoodbye("Good bye.");

				}
		};

		npc.setEntityClass("boynpc");
		npc.setPosition(114, 77);
		npc.initHP(100);
		zone.add(npc);
	}
}
