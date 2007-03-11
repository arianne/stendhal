package games.stendhal.server.maps.orril;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OL0_BridgeGuardNPC implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildNPC(zone);
	}


	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Stefan") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(62, 104));
				nodes.add(new Path.Node(63, 104));
				nodes.add(new Path.Node(64, 104));
				nodes.add(new Path.Node(65, 104));
				nodes.add(new Path.Node(64, 104));
				nodes.add(new Path.Node(63, 104));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, can I #help you?");
				addJob("I guard this bridge and check the road block stays in place.");
				addHelp("The road to Fado is closed until the city is safe.");
				addQuest("I'd like something more interesting to do, too.");
				addGoodbye("Goodbye and come back soon, I get bored here.");
			}
		};
		npc.setDescription("You see a bored looking guard.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "recruiter1npc");
		npc.set(62, 104);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
