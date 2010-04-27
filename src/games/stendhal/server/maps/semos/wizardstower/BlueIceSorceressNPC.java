package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Cassandra, the ice sorceress of the Wizards Tower
 *
 * @see games.stendhal.server.maps.quests.SorceressCassandraPlainQuest
 */
public class BlueIceSorceressNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildCassandra(zone);
	}

	private void buildCassandra(final StendhalRPZone zone) {
		final SpeakerNPC cassandra = new SpeakerNPC("Cassandra") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(37, 3));
				nodes.add(new Node(41, 3));
				nodes.add(new Node(33, 3));
				nodes.add(new Node(33, 9));
				nodes.add(new Node(31, 9));
				nodes.add(new Node(33, 9));
				nodes.add(new Node(33, 12));
				nodes.add(new Node(31, 12));
				nodes.add(new Node(31, 13));
				nodes.add(new Node(31, 11));
				nodes.add(new Node(33, 11));
				nodes.add(new Node(33, 9));
				nodes.add(new Node(31, 9));
				nodes.add(new Node(33, 9));
				nodes.add(new Node(33, 5));
				nodes.add(new Node(40, 5));
				nodes.add(new Node(40, 9));
				nodes.add(new Node(39, 9));
				nodes.add(new Node(39, 12));
				nodes.add(new Node(36, 12));
				nodes.add(new Node(40, 12));
				nodes.add(new Node(40, 3));
				nodes.add(new Node(41, 3));
				nodes.add(new Node(37, 3));
				nodes.add(new Node(37, 2));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings Stranger!");
				addHelp("");
				addReply("", "");
				addGoodbye("So long!");

			} //remaining behaviour defined in maps.quests.SorceressCassandraPlainQuest
		};

		cassandra.setDescription("You see Cassandra, a beautifull woman and powerfull sorceress.");
		cassandra.setEntityClass("bluesorceressnpc");
		cassandra.setPosition(37, 2);
		cassandra.initHP(100);
		zone.add(cassandra);
	}
}
