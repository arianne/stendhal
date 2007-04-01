package games.stendhal.server.maps.athor.ship;

import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.maps.quests.AthorFerryService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CargoWorkerNPC extends SpeakerNPCFactory {

	@Override
	protected SpeakerNPC instantiate(String name) {
		// The NPC is defined as a ferry announcer because he notifies
		// passengers when the ferry arrives or departs.
		SpeakerNPC npc = new AthorFerryService.FerryAnnouncerNPC(name) {
			public void onNewFerryState(int status) {
				if (status == AthorFerryService.AthorFerry.ANCHORED_AT_MAINLAND
						|| status == AthorFerryService.AthorFerry.ANCHORED_AT_ISLAND) {
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
		npc.addGreeting("Ahoy! Nice to see you in the cargo hold!");
		npc.addJob("I'm taking care of the cargo. My job would be much easier without all these #rats.");
		npc.addHelp("You could earn some money if you'd #offer me something to poison these damn #rats.");
		npc.addReply(Arrays.asList("rat", "rats"),
		        "These rats are everywhere. I wonder where they come from. I can't even kill them as fast as they come up.");
		
		Map<String, Integer> offerings = new HashMap<String, Integer>();
		offerings.put("poison", 40);
		offerings.put("toadstool", 60);
		offerings.put("greater_poison", 60);
		offerings.put("deadly_poison", 100);
		npc.addBuyer(new BuyerBehaviour(offerings));
		
		npc.addGoodbye("Please kill some rats on your way up!");
	}
}