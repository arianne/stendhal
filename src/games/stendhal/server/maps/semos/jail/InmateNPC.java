package games.stendhal.server.maps.semos.jail;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Semos Jail - Level -1
 * 
 * @author hendrik
 */
public class InmateNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildElf(zone);
	}

	private void buildElf(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Conual") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(13, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Let me out");
				addGoodbye();
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "militiaelfnpc");
		npc.set(13, 2);
		npc.initHP(100);
		npc.setDirection(Direction.DOWN);
		zone.add(npc);

	}
}
