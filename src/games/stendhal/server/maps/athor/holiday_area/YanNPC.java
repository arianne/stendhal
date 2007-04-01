package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class YanNPC implements ZoneConfigurator {

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
		SpeakerNPC yan = new SpeakerNPC("Yan") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// Yan doesn't move
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello stranger!");
				addQuest("I don't have a task right now, but in the next release I will get one...");
				addJob("Sorry, but on holiday I don't want to talk about work");
				addHelp("A cocktail bar will open on this island soon.");
				addGoodbye("See you later!");
			}
		};
		npcs.add(yan);

		zone.assignRPObjectID(yan);
		yan.put("class", "swimmer4npc");
		yan.set(190, 72);
		yan.setDirection(Direction.DOWN);
		yan.initHP(100);
		zone.add(yan);

	}
}
