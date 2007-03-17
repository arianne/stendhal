package games.stendhal.server.maps.fado.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds the city greeter NPC.
 *
 * @author timothyb89
 */
public class OL0_GreeterNPC implements ZoneConfigurator {
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
		buildNPC(zone, attributes);
	}


	//
	// OL0_GreeterNPC
	//

	private void buildNPC(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC GreeterNPC = new SpeakerNPC("Xhiphin Zohos") {
			@Override
					protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(39, 28));
				nodes.add(new Path.Node(23, 28));
				nodes.add(new Path.Node(23, 20));
				nodes.add(new Path.Node(40, 20));
				setPath(nodes, true);
					}
	
					@Override
							protected void createDialog() {
						addGreeting("Hello! Welcome to Fado City! Would you like to #learn about Fado?");
						addReply("learn", "Fado City is the jewl of the Faiumoni empire. It has a very important trade route with Orril and Semos to the North and #Sikhw to the South.");
						addReply("sikhw", "Sikhw is an old city that was conqured a long time ago. It is now nearly unreachable.");
						addJob("I greet all of the new-comers to Fado.");
						addHelp("You can head into the tavern to buy food, drinks, and other items.You can also visit the people in the houses, or visit the blacksmith or the city hotel.");
						//addSeller(new SellerBehaviour(shops.get("food&drinks")));
						addGoodbye("Bye.");
							}
		};
		npcs.add(GreeterNPC);
		zone.assignRPObjectID(GreeterNPC);
		GreeterNPC.setOutfit(new Outfit(05, 01, 06, 01));
		GreeterNPC.set(39, 28);
		GreeterNPC.initHP(1000);
		zone.add(GreeterNPC);
	}
}
