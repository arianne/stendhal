package games.stendhal.server.maps.athor.ship;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Factory for cargo worker on Athor Ferry. */
//TODO: take NPC definition elements which are currently in XML and include here
public class CargoWorkerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Ahoy! Nice to see you in the cargo hold!");
		npc.addJob("I'm taking care of the cargo. My job would be much easier without all these #rats.");
		npc.addHelp("You could earn some money if you'd #offer me something to poison these damn #rats.");
		npc.addReply(Arrays.asList("rat", "rats"),
		        "These rats are everywhere. I wonder where they come from. I can't even kill them as fast as they come up.");
		//prices are equal to the absolute value of their HP amount.
		final Map<String, Integer> offerings = new HashMap<String, Integer>();
		offerings.put("poison", 40);
		offerings.put("toadstool", 60);
		offerings.put("greater poison", 60);
		offerings.put("deadly poison", 100);
		offerings.put("mega poison", 500);
		offerings.put("disease poison", 2000);
		new BuyerAdder().add(npc, new BuyerBehaviour(offerings), true);

		npc.addGoodbye("Please kill some rats on your way up!");
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
