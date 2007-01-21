package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

public class Kanmararn implements ZoneConfigurator, IContent {
	private NPCList npcs;
	public Kanmararn() {
		this.npcs = NPCList.get();
		
		/**
		 * When ZoneConfigurator aware loader is used, remove this!!
		 */
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-6_kanmararn_city")),
			java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildKanmararnCityJames(zone);
		buildKanmararnCityHenry(zone);
	}


	private void buildKanmararnCityHenry(StendhalRPZone zone) {
		SpeakerNPC henry = new SpeakerNPC("Henry") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(57, 112));
				nodes.add(new Path.Node(59, 112));
				nodes.add(new Path.Node(59, 114));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				// Adds all the behaviour chat
				addGreeting("Ssshh! Silence or you will attract more #dwarves.");
				addJob("I'm a soldier in the army.");
				addGoodbye("Bye and be careful with all those dwarves around!");
				addHelp("I need help myself. I got seperated from my #group. Now I'm all alone.");
				add(ConversationStates.ATTENDING, Arrays.asList("dwarf", "dwarves"), ConversationStates.ATTENDING, "They are everywhere! Their #kingdom must be close.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("kingdom", "Kanmararn"), ConversationStates.ATTENDING, "Kanmararn, the legendary city of the #dwarves.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("group"), ConversationStates.ATTENDING, "The General sent five of us to explore this area in search for #treasure.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("treasure"), ConversationStates.ATTENDING, "A big treasure is rumored to be #somewhere in this dungeon.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("somewhere"), ConversationStates.ATTENDING, "If you #help me I might give you a clue.", null);
			}
		};

		// Adjust level/hp and add our new NPC to the game world
		henry.setLevel(5);
		henry.setHP(henry.getBaseHP() * 20 / 100);

		henry.put("class", "youngsoldiernpc");
		henry.setDescription("You see a young soldier who appears to be afraid.");
		npcs.add(henry);
		zone.assignRPObjectID(henry);
		zone.addNPC(henry);
		henry.set(57, 112);
	}

	private void buildKanmararnCityJames(StendhalRPZone zone) {
		// We create NPC James, the chief and last survivor of the quintet
		SpeakerNPC james = new SpeakerNPC("Sergeant James") {
			
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(66, 45));
				nodes.add(new Path.Node(66, 47));
				setPath(nodes, true);
			}
	
			@Override
			protected void createDialog() {
				// Adds all the behaviour chat
				addGreeting("Good day, adventurer!");
				addJob("I'm a Sergeant in the army.");
				addGoodbye("Good luck and better watch your back with all those dwarves around!");
			}
		};

		// Adjust level/hp and add our new NPC to the game world
		james.setLevel(20);
		james.setHP(james.getBaseHP() * 75 / 100);

		james.setDescription("You see an officer who bears many signs of recent battles.");
		james.put("class", "royalguardnpc");
		npcs.add(james);
		zone.assignRPObjectID(james);
		zone.addNPC(james);
		james.set(66, 45);
	}
}
