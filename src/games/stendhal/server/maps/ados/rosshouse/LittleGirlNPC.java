package games.stendhal.server.maps.ados.rosshouse;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Creates a normal version of Susi in the ross house.
 */
public class LittleGirlNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createGirlNPC(zone);
	}

	private void createGirlNPC(final StendhalRPZone zone) {
		
		if (System.getProperty("stendhal.minetown") != null) {
			return;
		}

		final SpeakerNPC npc = new SpeakerNPC("Susi") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 7));
				nodes.add(new Node(5, 7));
				nodes.add(new Node(5, 3));
				nodes.add(new Node(5, 8));
				nodes.add(new Node(10, 8));
				nodes.add(new Node(10, 12));
				nodes.add(new Node(12, 12));
				nodes.add(new Node(9, 12));
				nodes.add(new Node(9, 11));
				nodes.add(new Node(7, 11));
				nodes.add(new Node(7, 7));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				// TODO: Add different greetings depending on whether Susi's is a friend of the player or not
				addGreeting("Hello. Daddy must have left the house door open again. He's always doing that.");
				addJob("I am just a little girl.");
				addGoodbye("Have fun!");

				// TODO: Do we want to keep this? They used to have the same graphics but they are not anymore.
				addReply("debuggera", "She is my crazy twin sister.");

				addQuest("I might see you some time at the #Semos #Mine #Town #Revival #Weeks.");

				// Revival Weeks
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
					ConversationStates.ATTENDING,
					"During the Revival Weeks at the end of October we celebrate the old and now mostly dead Semos Mine Town.",
					null);
				
				// help
				addHelp("Have fun.");
			}
		};

		npc.setOutfit(new Outfit(04, 07, 32, 13));
		npc.setPosition(3, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}

}
