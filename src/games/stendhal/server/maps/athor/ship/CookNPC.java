package games.stendhal.server.maps.athor.ship;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;

import java.util.HashMap;
import java.util.Map;

/** Factory for cargo worker on Athor Ferry. */
//TODO: take NPC definition elements which are currently in XML and include here
public class CookNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Ahoy! Welcome to the galley!");
		npc.addJob("I'm running the galley on this ship. I #offer fine foods for the passengers and alcohol for the crew.");
		npc.addHelp("The crew mates drink beer and grog all day. But if you want some more exclusive drinks, go to the cocktail bar at Athor beach.");

		final Map<String, Integer> offerings = new HashMap<String, Integer>();
		offerings.put("beer", 10);
		offerings.put("wine", 15);
		// more expensive than in normal taverns
		offerings.put("ham", 100);
		offerings.put("pie", 150);
		new SellerAdder().addSeller(npc, new SellerBehaviour(offerings));

		npc.addGoodbye();
		new AthorFerry.FerryListener() {
			public void onNewFerryState(final Status status) {
				switch (status) {
				case ANCHORED_AT_MAINLAND:
				case ANCHORED_AT_ISLAND:
					npc.say("Attention: We have arrived!");
					break;
				default:
					npc.say("Attention: We have set sail!");
					break;
				}
			}
		};
	}
}
