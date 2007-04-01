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

public class TouristFromAdosNPC implements ZoneConfigurator {

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
		SpeakerNPC zara = new SpeakerNPC("Zara") {

			@Override
			protected void createPath() {
				// doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			public void say(String text) {
				// Zara doesn't move around because she's "lying" on her towel.
				say(text, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Nice to meet you!");
				addJob("I'm on holiday! Let's talk about anything else!");
				// TODO
				addHelp("Be careful! On this island is a desert where many adventurers found their death...");
				addGoodbye("I hope to see you soon!");
			}
		};
		npcs.add(zara);

		zone.assignRPObjectID(zara);
		zara.put("class", "swimmer8npc");
		zara.set(188, 32);
		zara.setDirection(Direction.DOWN);
		zara.initHP(100);
		zone.add(zara);

	}
}
