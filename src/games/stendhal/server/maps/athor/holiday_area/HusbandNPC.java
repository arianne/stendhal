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

public class HusbandNPC implements ZoneConfigurator {

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
		SpeakerNPC john = new SpeakerNPC("John") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// John doesn't move
				setPath(nodes, true);
			}

			@Override
			public void say(String text) {
				// John doesn't move around because he's "lying" on his towel.
				say(text, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING, "We have no tasks, we're here on holiday.", null);
				addJob("I am a coachman, but on this island there are no carriages!");
				addHelp("Don't try to talk to my wife, she is very shy.");
				addGoodbye("Bye!");
			}
		};
		npcs.add(john);

		zone.assignRPObjectID(john);
		john.put("class", "swimmer5npc");
		john.set(155, 43);
		john.setDirection(Direction.DOWN);
		john.initHP(100);
		zone.add(john);

	}
}
