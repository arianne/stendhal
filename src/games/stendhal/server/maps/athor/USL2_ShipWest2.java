package games.stendhal.server.maps.athor;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.maps.quests.AthorFerryService;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.NPCList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class USL2_ShipWest2 implements ZoneConfigurator {
	DefaultEntityManager manager = (DefaultEntityManager)
	StendhalRPWorld.get().getRuleManager().getEntityManager();

	/**
	 * Configure a zone.
	 * 
	 * @param 	zone		The zone to be configured.
	 * @param 	attributes	Configuration attributes.
	 */
	
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		createKlaas(zone, attributes);
	}

	private void createKlaas(StendhalRPZone zone,
			Map<String, String> attributes) {
		// Klaas is defined as a ferry announcer because she notifies
		// passengers when the ferry arrives or departs.
		AthorFerryService.FerryAnnouncerNPC klaas = new AthorFerryService.FerryAnnouncerNPC(
				"Klaas") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to the bucket
				nodes.add(new Path.Node(24, 41));
				// along the corridor
				nodes.add(new Path.Node(24, 34));
				// walk between barrels and boxes 
				nodes.add(new Path.Node(17, 34));
				// to the stairs
				nodes.add(new Path.Node(17, 38));
				// walk between the barrels
				nodes.add(new Path.Node(22, 38));
				// towards the bow
				nodes.add(new Path.Node(22, 41));
				setPath(nodes, true);
			}
	
			@Override
			protected void createDialog() {
				addGreeting("Ahoy! Nice to see you in the cargo hold!");
				addJob("I'm taking care of the cargo. My job would be much easier without all these #rats.");
				addHelp("You could earn some money if you'd #offer me something to poison these damn #rats.");
				addReply(Arrays.asList("rat", "rats"),
						"These rats are everywhere. I wonder where they come from. I can't even kill them as fast as they come up.");
				Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("poison", 40);
				offerings.put("toadstool", 60);
				offerings.put("greater_poison", 60);
				offerings.put("deadly_poison", 100);
				addBuyer(new BuyerBehaviour(offerings));
				addGoodbye("Please kill some rats on your way up!");
			}
			
			public void onNewFerryState(int status) {
				if (status == AthorFerryService.AthorFerry.ANCHORED_AT_MAINLAND
						|| status == AthorFerryService.AthorFerry.ANCHORED_AT_ISLAND) {
					say("Attention: We have arrived!");
				} else  {
					say("Attention: We have set sail!");
				}
			}

		};
		NPCList.get().add(klaas);
		zone.assignRPObjectID(klaas);
		klaas.put("class", "seller2npc");
		klaas.set(24, 41);
		klaas.initHP(100);
		zone.add(klaas);
	}
}
