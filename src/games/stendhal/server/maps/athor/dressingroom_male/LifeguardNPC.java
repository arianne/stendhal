package games.stendhal.server.maps.athor.dressingroom_male;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Dressing rooms at the Athor island beach (Inside / Level 0).
 *
 * @author daniel
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class LifeguardNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addJob("I'm one of the lifeguards at this beach. And as you can see, I also take care of the men's dressing room.");
		npc.addHelp("Just tell me if you want to #borrow #trunks!");
		npc.addGoodbye("Have fun!");

		final Map<String, Integer> priceList = new HashMap<String, Integer>();
		priceList.put("trunks", 5);
		final OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(priceList);
		new OutfitChangerAdder().addOutfitChanger(npc, behaviour, "borrow");

		// stuff needed for the SuntanCreamForZara quest
		// (uses sorted TreeMap instead of HashMap)
		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("arandula", 1);
		requiredResources.put("kokuda", 1);
		requiredResources.put("minor potion", 1);

		final ProducerBehaviour mixerBehaviour = new ProducerBehaviour("david_mix_cream",
				"mix", "suntan cream", requiredResources, 10 * 60);

		new ProducerAdder().addProducer(npc, mixerBehaviour, "Hallo!");

		npc.addReply(
		        Arrays.asList("suntan", "cream", "suntan cream"),
		        "Pam's and mine suntan cream is famous all over the island. But the way to the labyrinth entrance is blocked, so we can't get all the ingredients we need. If you bring me the things we need, I can #mix our special suntan cream for you.");

		npc.addReply("arandula", "Arandula is a herb which is growing around Semos.");

		npc.addReply(
		        "kokuda",
		        "We can't find the Kokuda herb which is growing on this island, because the entrance of the labyrinth, where you can find this herb, is blocked.");

		npc.addReply("minor potion", "It's a small bottle full of potion. You can buy it at several places.");
	};
}
