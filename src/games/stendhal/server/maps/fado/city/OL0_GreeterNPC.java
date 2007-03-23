package games.stendhal.server.maps.fado.city;

import games.stendhal.common.Direction;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds the city greeter NPC.
 *
 * @author timothyb89
 */
public class OL0_GreeterNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	private ShopList shops = ShopList.get();

	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone, attributes);
		buildNunNPC(zone, attributes);
	}

	//
	// OL0_GreeterNPC
	//

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
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
				addReply(
				        "learn",
				        "Fado City is the jewel of the Faiumoni empire. It has a very important trade route with Orril and Semos to the North and #Sikhw to the South.");
				addReply("sikhw",
				        "Sikhw is an old city that was conquered a long time ago. It is now nearly unreachable.");
				addJob("I greet all of the new-comers to Fado. I can #offer you a scroll if you'd like to come back here again.");
				addHelp("You can head into the tavern to buy food, drinks, and other items. You can also visit the people in the houses, or visit the blacksmith or the city hotel.");
				addSeller(new SellerBehaviour(shops.get("fadoscrolls")));
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

	//
	// A Nun NPC outside church
	//
	private void buildNunNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC nunnpc = new SpeakerNPC("Sister Benedicta") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// does not move
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to this place of worship.");
				addHelp("I don't know what you need, dear child.");
				addJob("I am a nun. But this is my life, not my work.");
				addQuest("The great quest of all life is to be #married.");
				addReply(
				        "married",
				        "When you have found your partner, and you are very sure that you want to be joined together in matrimony, tell a suitable #priest");
				addReply(
				        "priest",
				        "A priest will say the rites in the ceremony. You will need to book a time when the priest can come. Try asking /support and a response may come.");
				addGoodbye("Goodbye, may peace be with you.");
			}
		};
		nunnpc.setDescription("You see Sister Benedicta, a holy nun.");
		npcs.add(nunnpc);
		zone.assignRPObjectID(nunnpc);
		nunnpc.put("class", "nunnpc");
		nunnpc.setDirection(Direction.RIGHT);
		nunnpc.set(53, 53);
		nunnpc.initHP(100);
		zone.add(nunnpc);
	}
}
