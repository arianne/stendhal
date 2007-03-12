package games.stendhal.server.maps.athor;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Ados Tavern (Inside / Level 0)
 *
 * @author hendrik
 */
public class USL1_ShipWest2 implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildTavern(zone, attributes);
	}


	private void buildTavern(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC galleyMaid = new SpeakerNPC("Foo") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to the oven
				nodes.add(new Path.Node(27, 27));
				// to the table
				nodes.add(new Path.Node(27, 30));
				// to the dining room
				nodes.add(new Path.Node(18, 30));
				// to the barrel
				nodes.add(new Path.Node(28, 30));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Ahoy! Welcome to the galley!");
				addJob("I'm running the galley on this ship. I #offer fine foods for the passengers and alcohol for the crew.");
				addHelp("The crew mates drink beer and grog all day. But if you want some more exclusive drinks, go to the cocktail bar at Athor beach.");
				Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("beer", 10);
				offerings.put("wine", 15);
				// more expensive than in normal taverns 
				offerings.put("ham", 100);
				offerings.put("pie", 150);
				addSeller(new SellerBehaviour(offerings));
				addGoodbye();
			}
		};
		npcs.add(galleyMaid);
		zone.assignRPObjectID(galleyMaid);
		galleyMaid.put("class", "tavernbarmaidnpc");
		galleyMaid.set(27, 27);
		galleyMaid.initHP(100);
		zone.addNPC(galleyMaid);
	}
}
