package games.stendhal.server.maps.magic.school;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Teacher NPC who flies on a broomstick
 * 
 * @author kymara
 */
public class TeacherNPC implements ZoneConfigurator {

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
		SpeakerNPC npc = new SpeakerNPC("Esolte Vietta") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(29, 19));
				nodes.add(new Node(36, 19));
				nodes.add(new Node(36, 21));
				nodes.add(new Node(29, 21));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES, null,
						ConversationStates.IDLE,
						"Sit down, shut up, and watch me!", null);
			}

		};

		npc.setDescription("You see a witch flying on a broomstick. She appears to be instructing some pupils.");
		npc.setEntityClass("witch3npc");
		npc.setPosition(29, 19);
		npc.initHP(100);
		zone.add(npc);
	}
}
