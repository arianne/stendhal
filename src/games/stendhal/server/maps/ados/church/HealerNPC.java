package games.stendhal.server.maps.ados.church;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

import java.util.Map;
import java.util.TreeMap;

/**
 * The healer (original name: Valo). He makes mega potions. 
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class HealerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
	    npc.addJob("Long ago I was a priest of this church. But my #ideas were not approved of by all."); 
	    npc.addReply("ideas",
		        "I have read many texts and learnt of strange ways. My healing powers became so strong I can now #concoct a special #'mega potion' for warriors like you.");
	    npc.addReply("giant heart",
		        "Giants dwell in caves east of here. Good luck slaying those beasts ...");
		npc.addOffer("I can #concoct a #'mega potion' for you. I will need a #'giant heart' for this.");
	    npc.addReply("mega potion", "It is a powerful elixir. If you want one, ask me to #'concoct 1 mega potion'.");
	    npc.addReply("money", "That is your own concern. We of the cloth need not scurry around to make cash.");    
	    npc.addHelp("If you want to become wise like me, you should visit a library. There is much to learn and #ideas to explore.");
	    npc.addGoodbye("Fare thee well.");

		// Valo makes mega potions if you bring giant heart and money
		// (uses sorted TreeMap instead of HashMap)
		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("money", 20);
		requiredResources.put("giant heart", 1);
		final ProducerBehaviour behaviour = new ProducerBehaviour("valo_concoct_potion",
				"concoct", "mega potion", requiredResources, 2 * 60);

		new ProducerAdder().addProducer(npc, behaviour,
		        "Greetings, young one. I #heal and I #help.");
		// charge (1*the player level + 1) to heal
		new HealerAdder().addHealer(npc, -1);
	}
}
