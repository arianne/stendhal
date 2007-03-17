package games.stendhal.server.maps.nalwor.royal;

import games.stendhal.server.StendhalRPWorld;
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

public class IL0_MayorNPC implements ZoneConfigurator {
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
		SpeakerNPC npc = new SpeakerNPC("Maerion") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 22));
				nodes.add(new Path.Node(13, 22));
				nodes.add(new Path.Node(13, 24));
				nodes.add(new Path.Node(17, 24));
				nodes.add(new Path.Node(17, 22));
				nodes.add(new Path.Node(21, 22));
				nodes.add(new Path.Node(21, 26));
				nodes.add(new Path.Node(17, 26));
				nodes.add(new Path.Node(17, 24));
				nodes.add(new Path.Node(13, 24));
				nodes.add(new Path.Node(13, 22));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello. You are brave, to stand before me.");
				addJob("You dare ask, little human?!");
				addHelp("Well, perhaps you can help me with a #problem I see brewing. But not yet, not yet...");
				addQuest("Thanks, one day, I will remember that you offered. I may need you.");
				add(ConversationStates.ATTENDING,
					"problem",
					null,
					ConversationStates.ATTENDING,
				    "Here are no dark elves, believe me! Me?! no, no, no, I'm just well tanned...",
					null);
				addGoodbye("Farewell, human.");
			}
		};
		npc.setDescription("You see a regal elf. Something about him makes you uneasy.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "elfmayornpc");
		npc.set(9, 22);
		npc.initHP(100);
		zone.add(npc);

	
	}
}
