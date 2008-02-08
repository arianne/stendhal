package games.stendhal.server.maps.nalwor.hell;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates the elementals npcs in hell.
 *
 * @author kymara
 */
public class ElementalsNPCs implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPCs(zone);
	}

	private void buildNPCs(StendhalRPZone zone) {
		String[] names = {"Savanka", "Xeoilia", "Azira"};
		Node[] start = new Node[] { new Node(117, 5), new Node(119, 8), new Node(118, 10) };
		for (int i = 0; i < 3; i++) {
			SpeakerNPC npc = new SpeakerNPC(names[i]) {

				@Override
				protected void createPath() {
					List<Node> nodes = new LinkedList<Node>();
					nodes.add(new Node(117, 5));
					nodes.add(new Node(117, 7));
					nodes.add(new Node(118, 7));
					nodes.add(new Node(118, 8));
					nodes.add(new Node(119, 8));
					nodes.add(new Node(119, 10));
					nodes.add(new Node(125, 10));
					nodes.add(new Node(125, 11));
					nodes.add(new Node(118, 11));
					nodes.add(new Node(118, 10));
					nodes.add(new Node(118, 9));
					nodes.add(new Node(117, 9));
					nodes.add(new Node(117, 8));
					nodes.add(new Node(116, 8));
					nodes.add(new Node(116, 5));
					setPath(new FixedPath(nodes, true));
				}

				@Override
				protected void createDialog() {
					add(
			     		ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						ConversationStates.IDLE,
						"Speak not to us, the harbingers of Hell!",
						null);
			
				}
			};
			npc.setEntityClass("fireelementalnpc");
			npc.setPosition(start[i].getX(), start[i].getY());
			npc.setDirection(Direction.DOWN);
			npc.initHP(100);
			zone.add(npc);
		}
	}
}
