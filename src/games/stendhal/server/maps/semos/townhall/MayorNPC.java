package games.stendhal.server.maps.semos.townhall;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

public class MayorNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosTownhallAreaMayor(zone);
	}

	/**
	 * Adding a a Mayor to the townhall who gives out daily quests
	 */
	private void buildSemosTownhallAreaMayor(StendhalRPZone zone) {
		// We create an NPC
		SpeakerNPC npc = new SpeakerNPC("Mayor Sakhs") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(13, 2));
				nodes.add(new Path.Node(19, 2));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome citizen! Do you need #help?");
				addJob("I'm the mayor of Semos village.");
				addHelp("You will find a lot of people in Semos that offer you help on different topics.");
				addGoodbye("Have a good day and enjoy your stay!");
			}
		};

		npc.put("class", "mayornpc");
		npc.set(13, 2);
		npc.initHP(100);
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		zone.add(npc);
	}
}
