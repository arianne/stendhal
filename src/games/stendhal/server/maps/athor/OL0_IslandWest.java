package games.stendhal.server.maps.athor;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OL0_IslandWest implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildBeachArea(zone, attributes);
	}

	private void buildBeachArea(StendhalRPZone zone,
			Map<String, String> attributes) {
		SpeakerNPC cyk = new SpeakerNPC("Cyk") {
			@Override
			protected void createPath() {
				// doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			public void say(String text) {
				// Cyk doesn't move around because he's "lying" on his towel.
				say(text, false);
			}
			
			@Override
			protected void createDialog() {
				addGreeting("Hey there!");
				add(ConversationStates.ATTENDING,
						ConversationPhrases.QUEST_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"I don't have a task for you, I'm perfectly happy!",
						null);
				addJob("Don't remind me of my job, I'm on holiday!");
				addHelp("In the desert there is dangerous quicksand.");
				addGoodbye("Bye! I'll stock up on some more sunshine.");
			}
		};
		npcs.add(cyk);
		
		zone.assignRPObjectID(cyk);
		cyk.put("class", "swimmer1npc");
		cyk.set(172, 39);
		cyk.setDirection(Direction.DOWN);
		cyk.initHP(100);
		zone.add(cyk);
		
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
