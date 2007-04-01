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

public class DiverNPC implements ZoneConfigurator {

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
		SpeakerNPC dorinel = new SpeakerNPC("Dorinel") {

			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// Dorinel is swimming in the sea
				nodes.add(new Path.Node(169, 21));
				nodes.add(new Path.Node(169, 28));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hallo, my friend!");
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING, "No, thank you, I do not need help!", null);
				addJob("I am a diver, but I cannot see a single fish at the moment!");
				addHelp("I like the swimsuits which you can get in the dressing rooms at the beach.");
				addGoodbye("Bye!");
			}
		};
		npcs.add(dorinel);

		zone.assignRPObjectID(dorinel);
		dorinel.put("class", "swimmer2npc");
		dorinel.set(169, 28);
		dorinel.setDirection(Direction.DOWN);
		dorinel.initHP(100);
		zone.add(dorinel);

	}
}
