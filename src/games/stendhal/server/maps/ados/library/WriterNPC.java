package games.stendhal.server.maps.ados.library;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class WriterNPC implements ZoneConfigurator {
	final static String npc_name = "Marie-Henri";

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC(npc_name) {

			@Override
			protected void createPath() {
				// setPath(null);
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(19, 3));
				nodes.add(new Node(19, 8));
				nodes.add(new Node(18, 8));
				nodes.add(new Node(18, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Bonjour!");
				addJob("I am a famous french writer. I spend my time writing novels and reading the thoughts of other keen thinkers in this library.");
				addOffer("I do not sell anything.");
				addHelp("If you want to get as smart as I am, consult the #Wikipedian.");
				addReply("wikipedian", "I mean the old wise guy wandering around in this library. Ask him about any topic you want to know more about.");
				addGoodbye("Au revoir!");
			}
		};

		npc.setEntityClass("writernpc");
		npc.setDescription("You see " + npc_name + ", a famous french writer.");
		npc.setPosition(19, 3);
		npc.initHP(100);

		zone.add(npc);
	}
}
