package games.stendhal.server.maps.ados.outside;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CloaksCollectorNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildZooSub3Area(zone, attributes);
	}

	private void buildZooSub3Area(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Bario") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				// to stove
				nodes.add(new Node(7, 43));
				// to table
				nodes.add(new Node(7, 51));
				// around couch
				nodes.add(new Node(14, 56));
				nodes.add(new Node(22, 56));
				// into the floor
				nodes.add(new Node(18, 49));
				nodes.add(new Node(19, 41));
				// into the bathroom
				nodes.add(new Node(39, 41));
				// into the floor
				nodes.add(new Node(18, 41));
				// into the bedroom
				nodes.add(new Node(18, 28));
				// to the chest
				nodes.add(new Node(17, 23));
				// through the floor
				nodes.add(new Node(18, 33));
				nodes.add(new Node(18, 50));
				// back to the kitchen
				nodes.add(new Node(7, 50));
				nodes.add(new Node(4, 43));
				nodes.add(new Node(4, 46));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("There is a quite high unemployment rate down here.");
				addHelp("I have heard rumors that mysterious creatures with blue cloaks are haunting the swamp south of here.");
				addGoodbye();
				// remaining behaviour is defined in maps.quests.CloaksForBario.
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "beardmannpc");
		npc.set(4, 46);
		npc.initHP(100);
		zone.add(npc);
	}
}
