package games.stendhal.server.maps.ados.city;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates the NPCs and portals in Ados City.
 *
 * @author hendrik
 */
public class KidsNPCs implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildKids(zone);
	}

	private void buildKids(StendhalRPZone zone) {
		String[] names = { "Jens", "George", "Anna" };
		String[] classes = { "kid3npc", "kid4npc", "kid5npc" };
		Path.Node[] start = new Path.Node[] { new Path.Node(40, 28), new Path.Node(40, 40), new Path.Node(45, 28) };
		for (int i = 0; i < 3; i++) {
			SpeakerNPC npc = new SpeakerNPC(names[i]) {

				@Override
				protected void createPath() {
					List<Path.Node> nodes = new LinkedList<Path.Node>();
					nodes.add(new Path.Node(40, 28));
					nodes.add(new Path.Node(40, 31));
					nodes.add(new Path.Node(34, 31));
					nodes.add(new Path.Node(34, 35));
					nodes.add(new Path.Node(39, 35));
					nodes.add(new Path.Node(39, 40));
					nodes.add(new Path.Node(40, 40));
					nodes.add(new Path.Node(40, 38));
					nodes.add(new Path.Node(45, 38));
					nodes.add(new Path.Node(45, 42));
					nodes.add(new Path.Node(51, 42));
					nodes.add(new Path.Node(51, 36));
					nodes.add(new Path.Node(46, 36));
					nodes.add(new Path.Node(46, 29));
					nodes.add(new Path.Node(45, 29));
					nodes.add(new Path.Node(45, 28));
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
			npcs.add(npc);

			zone.assignRPObjectID(npc);
			npc.put("class", classes[i]);
			npc.set(start[i].x, start[i].y);
			npc.setDirection(Direction.DOWN);
			npc.initHP(100);
			zone.add(npc);
		}
	}
}
