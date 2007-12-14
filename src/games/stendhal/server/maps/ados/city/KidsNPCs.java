package games.stendhal.server.maps.ados.city;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates the NPCs and portals in Ados City.
 * 
 * @author hendrik
 */
public class KidsNPCs implements ZoneConfigurator {
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
		buildKids(zone);
	}

	private void buildKids(StendhalRPZone zone) {
		String[] names = { "Jens", "George", "Anna" };
		String[] classes = { "kid3npc", "kid4npc", "kid5npc" };
		Node[] start = new Node[] { new Node(40, 29), new Node(40, 41),
				new Node(45, 29) };
		for (int i = 0; i < 3; i++) {
			SpeakerNPC npc = new SpeakerNPC(names[i]) {

				@Override
				protected void createPath() {
					List<Node> nodes = new LinkedList<Node>();
					nodes.add(new Node(40, 29));
					nodes.add(new Node(40, 32));
					nodes.add(new Node(34, 32));
					nodes.add(new Node(34, 36));
					nodes.add(new Node(39, 36));
					nodes.add(new Node(39, 41));
					nodes.add(new Node(40, 41));
					nodes.add(new Node(40, 39));
					nodes.add(new Node(45, 39));
					nodes.add(new Node(45, 43));
					nodes.add(new Node(51, 43));
					nodes.add(new Node(51, 37));
					nodes.add(new Node(46, 37));
					nodes.add(new Node(46, 30));
					nodes.add(new Node(45, 30));
					nodes.add(new Node(45, 29));
					setPath(new FixedPath(nodes, true));
				}

				@Override
				protected void createDialog() {
					// Anna is special because she has a quest
					if (!this.getName().equals("Anna")) {
						add(
								ConversationStates.IDLE,
								ConversationPhrases.GREETING_MESSAGES,
								ConversationStates.IDLE,
								"Mummy said, we are not allowed to talk to strangers. She is worried about that lost girl. Bye.",
								null);
					}
					addGoodbye("Bye bye!");
				}
			};

			npc.setEntityClass(classes[i]);
			npc.setPosition(start[i].getX(), start[i].getY());
			npc.setDirection(Direction.DOWN);
			npc.initHP(100);
			zone.add(npc);
		}
	}
}
