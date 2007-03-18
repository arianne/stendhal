package games.stendhal.server.maps.fado.hotel;

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
 * Builds the hotel greeter NPC.
 *
 * @author timothyb89
 */
public class IL0_GreeterNPC implements ZoneConfigurator {
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
	// IL0_GreeterNPC
	//

	private void buildNPC(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC greeterNPC = new SpeakerNPC("Linda") {
			@Override
					protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(16, 49));
				nodes.add(new Path.Node(27, 49));
				setPath(nodes, true);
					}

					@Override
							protected void createDialog() {
						addGreeting("Hello! Welcome to the Fado City Hotel! Can I #help you?");

						addJob("I am the hotel receptionist.");
						addHelp("You can #reserve a room if you'd like, or #explore the hotel.");
                                                addReply("reserve","Sorry, but the hotel is still under construction and you can not reserve a room yet.");
                                                addReply("explore", "You can take a stop at the hotel's mini restaurant.");
						//addSeller(new SellerBehaviour(shops.get("food&drinks")));
						addGoodbye("Bye.");
							}
		};

		npcs.add(greeterNPC);
		zone.assignRPObjectID(greeterNPC);
		greeterNPC.put("class", "hotelreceptionistnpc");
		greeterNPC.set(16, 48);
		greeterNPC.initHP(1000);
		zone.add(greeterNPC);
	}
}
