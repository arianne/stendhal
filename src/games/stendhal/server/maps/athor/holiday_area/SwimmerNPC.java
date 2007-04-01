package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SwimmerNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildBeachArea(zone, attributes);
	}

	private void buildBeachArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC enrique = new SpeakerNPC("Enrique") {

			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// Enrique is swimming in the pool
				nodes.add(new Path.Node(195, 68));
				nodes.add(new Path.Node(195, 63));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Don't disturb me, I'm trying to establish a record!");
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING, "I don't have a task for you, I'm too busy.", null);
				addJob("I am a swimmer!");
				addHelp("Try the diving board! It's fun!");
				addGoodbye("Bye!");
			}
		};
		npcs.add(enrique);

		zone.assignRPObjectID(enrique);
		enrique.put("class", "swimmer3npc");
		enrique.set(195, 63);
		enrique.setDirection(Direction.DOWN);
		enrique.initHP(100);
		zone.add(enrique);

	}
}
