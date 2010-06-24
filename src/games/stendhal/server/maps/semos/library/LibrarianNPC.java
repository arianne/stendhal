package games.stendhal.server.maps.semos.library;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LibrarianNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSemosLibraryArea(zone, attributes);
	}

	private void buildSemosLibraryArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Ceryl") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(28, 12));
				nodes.add(new Node(28, 21));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the librarian.");
				addHelp("Hey, read a book and help yourself! You're never too old to stop learning.");
				addGoodbye();
			}
		};

		npc.setEntityClass("investigatornpc");
		npc.setDescription("You see Ceryl, a slightly crazed looking librarian.");
		npc.setPosition(28, 12);
		npc.initHP(100);
		zone.add(npc);
	}
}
