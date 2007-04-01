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

public class WifeNPC implements ZoneConfigurator {

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
		SpeakerNPC jane = new SpeakerNPC("Jane") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// John doesn't move
				setPath(nodes, true);
			}

			@Override
			public void say(String text) {
				// Jane doesn't move around because she's "lying" on her towel.
				say(text, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				addGoodbye("Bye!");
			}
		};
		npcs.add(jane);

		zone.assignRPObjectID(jane);
		jane.put("class", "swimmer6npc");
		jane.set(156, 43);
		jane.setDirection(Direction.DOWN);
		jane.initHP(100);
		zone.add(jane);

	}
}
