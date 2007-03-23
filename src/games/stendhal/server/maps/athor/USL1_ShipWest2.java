package games.stendhal.server.maps.athor;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.maps.quests.AthorFerryService;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class USL1_ShipWest2 implements ZoneConfigurator {

	DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld.get().getRuleManager().getEntityManager();

	/**
	 * Configure a zone.
	 * 
	 * @param 	zone		The zone to be configured.
	 * @param 	attributes	Configuration attributes.
	 */

	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		createLaura(zone, attributes);
	}

	private void createLaura(StendhalRPZone zone, Map<String, String> attributes) {
		// Laura is defined as a ferry announcer because she notifies
		// passengers when the ferry arrives or departs.
		AthorFerryService.FerryAnnouncerNPC laura = new AthorFerryService.FerryAnnouncerNPC("Laura") {

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

			public void onNewFerryState(int status) {
				if (status == AthorFerryService.AthorFerry.ANCHORED_AT_MAINLAND
				        || status == AthorFerryService.AthorFerry.ANCHORED_AT_ISLAND) {
					say("Attention: We have arrived!");
				} else {
					say("Attention: We have set sail!");
				}
			}

		};
		NPCList.get().add(laura);
		zone.assignRPObjectID(laura);
		laura.put("class", "tavernbarmaidnpc");
		laura.set(27, 27);
		laura.initHP(100);
		zone.add(laura);
	}
}
