package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.NPCOwnedChest;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class USL3_OutsideNorthWest implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildZooSub3Area(zone, attributes);
	}


	private void buildZooSub3Area(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Bario") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to stove
				nodes.add(new Path.Node(7, 43));
				// to table
				nodes.add(new Path.Node(7, 51));
				// around couch
				nodes.add(new Path.Node(14, 56));
				nodes.add(new Path.Node(22, 56));
				// into the floor
				nodes.add(new Path.Node(18, 49));
				nodes.add(new Path.Node(19, 41));
				// into the bathroom
				nodes.add(new Path.Node(39, 41));
				// into the floor
				nodes.add(new Path.Node(18, 41));
				// into the bedroom
				nodes.add(new Path.Node(18, 28));
				// to the chest
				nodes.add(new Path.Node(17, 23));
				// through the floor
				nodes.add(new Path.Node(18, 33));
				nodes.add(new Path.Node(18, 50));
				// back to the kitchen
				nodes.add(new Path.Node(7, 50));
				nodes.add(new Path.Node(4, 43));
				nodes.add(new Path.Node(4, 46));
				setPath(nodes, true);
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
		zone.addNPC(npc);
	}
}
