package games.stendhal.server.maps.nalwor.bank;

import games.stendhal.common.Direction;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds the nalwor bank npcs.
 *
 * @author kymara
 */
public class IL0_BankNPC implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildoldNPC(zone, attributes);
		buildladyNPC(zone, attributes);
	}


	//
	// name inspired by a name in lotr
	// I want him to complain if someone steals something from his chest: they should be sent to elf jail.

	private void buildoldNPC(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC oldnpc = new SpeakerNPC("Grafindle") {
			@Override
					protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
					}
	
					@Override
							protected void createDialog() {
						addGreeting("Greetings. If you need #help, please ask.");
						addJob("I work here in the bank.");
						addHelp("That room has two chests owned by this bank and two owned by Semos bank, so you can access all your savings.");
						addQuest("I ask only that you are honest");
						addGoodbye("Goodbye, young human.");
							}
		};
		npcs.add(oldnpc);
		zone.assignRPObjectID(oldnpc);
		oldnpc.setDirection(Direction.DOWN);
		oldnpc.put("class", "elfbankeroldnpc");
		oldnpc.set(13, 16);
		oldnpc.initHP(100);
		zone.add(oldnpc);
	}
	
	//
	// Ariannyddion is welsh for bank, so ...
	//
	private void buildladyNPC(StendhalRPZone zone,
			 Map<String, String> attributes) {
		SpeakerNPC ladynpc = new SpeakerNPC("Nnyddion") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(26, 29));
				nodes.add(new Path.Node(16, 29));
				nodes.add(new Path.Node(16, 30));
				nodes.add(new Path.Node(17, 30));
				nodes.add(new Path.Node(17, 29));
				nodes.add(new Path.Node(26, 29));
				setPath(nodes, true);
			}
			@Override
			protected void createDialog() {
				addGreeting("Welcome to Nalwor Bank. I'm here to #help.");
				addHelp("You can deposit your items in the chests in that small room behind me. The two chests on the right are under Semos management.");
				addJob("I help customers of the bank, elves and even humans.");
				addQuest("I don't need anything, thank you.");
				addGoodbye("Goodbye, thank you for your custom.");
			}
		};
		ladynpc.setDescription("You see a pretty female elf in a beautiful dress.");
		npcs.add(ladynpc);
		zone.assignRPObjectID(ladynpc);
		ladynpc.put("class", "elfbankladynpc");
		ladynpc.setDirection(Direction.DOWN);
		ladynpc.set(17, 30);
		ladynpc.initHP(100);
		zone.add(ladynpc);
	}
}
