package games.stendhal.server.maps.athor.ship;

import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

import java.util.HashMap;
import java.util.Map;

/** Factory for cargo worker on Athor Ferry */
public class CookNPC extends SpeakerNPCFactory {

	@Override
	protected SpeakerNPC instantiate(String name) {
		// The NPC is defined as a ferry announcer because he notifies
		// passengers when the ferry arrives or departs.
		SpeakerNPC npc = new AthorFerry.FerryAnnouncerNPC(name) {
			public void onNewFerryState(int status) {
				if (status == AthorFerry.ANCHORED_AT_MAINLAND
						|| status == AthorFerry.ANCHORED_AT_ISLAND) {
					say("Attention: We have arrived!");
				} else {
					say("Attention: We have set sail!");
				}
			}
		};
		return npc;
	}

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addGreeting("Ahoy! Welcome to the galley!");
		npc.addJob("I'm running the galley on this ship. I #offer fine foods for the passengers and alcohol for the crew.");
		npc.addHelp("The crew mates drink beer and grog all day. But if you want some more exclusive drinks, go to the cocktail bar at Athor beach.");
		
		Map<String, Integer> offerings = new HashMap<String, Integer>();
		offerings.put("beer", 10);
		offerings.put("wine", 15);
		// more expensive than in normal taverns 
		offerings.put("ham", 100);
		offerings.put("pie", 150);
		npc.addSeller(new SellerBehaviour(offerings));
		
		npc.addGoodbye();
		AthorFerry.get().addListener(
				(AthorFerry.FerryAnnouncerNPC) npc);
	}
}