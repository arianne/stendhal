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

public class WalkingWomanNPC implements ZoneConfigurator {

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
		SpeakerNPC kelicia = new SpeakerNPC("Kelicia") {

			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// Kelicia is walking along the coast
				nodes.add(new Path.Node(133, 49));
				nodes.add(new Path.Node(134, 48));
				nodes.add(new Path.Node(134, 47));
				nodes.add(new Path.Node(136, 47));
				nodes.add(new Path.Node(136, 46));
				nodes.add(new Path.Node(139, 46));
				nodes.add(new Path.Node(139, 45));
				nodes.add(new Path.Node(141, 45));
				nodes.add(new Path.Node(141, 44));
				nodes.add(new Path.Node(143, 44));
				nodes.add(new Path.Node(143, 43));
				nodes.add(new Path.Node(145, 43));
				nodes.add(new Path.Node(145, 42));
				nodes.add(new Path.Node(147, 42));
				nodes.add(new Path.Node(147, 41));
				nodes.add(new Path.Node(148, 41));
				nodes.add(new Path.Node(148, 40));
				nodes.add(new Path.Node(150, 40));
				nodes.add(new Path.Node(150, 39));
				nodes.add(new Path.Node(152, 39));
				nodes.add(new Path.Node(152, 38));
				nodes.add(new Path.Node(154, 38));
				nodes.add(new Path.Node(154, 37));
				nodes.add(new Path.Node(155, 37));
				nodes.add(new Path.Node(155, 36));
				nodes.add(new Path.Node(157, 36));
				nodes.add(new Path.Node(157, 35));
				nodes.add(new Path.Node(159, 35));
				nodes.add(new Path.Node(159, 34));
				nodes.add(new Path.Node(162, 34));
				nodes.add(new Path.Node(162, 33));
				nodes.add(new Path.Node(163, 33));
				nodes.add(new Path.Node(163, 32));
				nodes.add(new Path.Node(169, 32));
				nodes.add(new Path.Node(169, 31));
				nodes.add(new Path.Node(173, 31));
				nodes.add(new Path.Node(173, 30));
				nodes.add(new Path.Node(175, 30));
				nodes.add(new Path.Node(175, 29));
				nodes.add(new Path.Node(180, 29));
				nodes.add(new Path.Node(180, 28));
				nodes.add(new Path.Node(204, 28));

				// the same way back
				nodes.add(new Path.Node(180, 28));
				nodes.add(new Path.Node(180, 29));
				nodes.add(new Path.Node(175, 29));
				nodes.add(new Path.Node(175, 30));
				nodes.add(new Path.Node(173, 30));
				nodes.add(new Path.Node(173, 31));
				nodes.add(new Path.Node(169, 31));
				nodes.add(new Path.Node(169, 32));
				nodes.add(new Path.Node(163, 32));
				nodes.add(new Path.Node(163, 33));
				nodes.add(new Path.Node(162, 33));
				nodes.add(new Path.Node(162, 34));
				nodes.add(new Path.Node(159, 34));
				nodes.add(new Path.Node(159, 35));
				nodes.add(new Path.Node(157, 35));
				nodes.add(new Path.Node(157, 36));
				nodes.add(new Path.Node(155, 36));
				nodes.add(new Path.Node(155, 37));
				nodes.add(new Path.Node(154, 37));
				nodes.add(new Path.Node(154, 38));
				nodes.add(new Path.Node(152, 38));
				nodes.add(new Path.Node(152, 39));
				nodes.add(new Path.Node(150, 39));
				nodes.add(new Path.Node(150, 40));
				nodes.add(new Path.Node(148, 40));
				nodes.add(new Path.Node(148, 41));
				nodes.add(new Path.Node(147, 41));
				nodes.add(new Path.Node(147, 42));
				nodes.add(new Path.Node(145, 42));
				nodes.add(new Path.Node(145, 43));
				nodes.add(new Path.Node(143, 43));
				nodes.add(new Path.Node(143, 44));
				nodes.add(new Path.Node(141, 44));
				nodes.add(new Path.Node(141, 45));
				nodes.add(new Path.Node(139, 45));
				nodes.add(new Path.Node(139, 46));
				nodes.add(new Path.Node(136, 46));
				nodes.add(new Path.Node(136, 47));
				nodes.add(new Path.Node(134, 47));

				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				addQuest("I have no jobs for you, my friend");
				addJob("I'm just walking along the coast!");
				addHelp("I cannot help you...I'm just a girl...");
				addGoodbye("Bye!");
			}
		};
		npcs.add(kelicia);

		zone.assignRPObjectID(kelicia);
		kelicia.put("class", "swimmer7npc");
		kelicia.set(133, 48);
		kelicia.setDirection(Direction.DOWN);
		kelicia.initHP(100);
		zone.add(kelicia);

	}
}
