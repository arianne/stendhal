package games.stendhal.server.maps.semos.catacombs;

import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A sick vampire who will fill your goblet for a quest.
 *
 */
public class SickVampireNPC extends SpeakerNPCFactory {

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addGoodbye("*cough* ... farewell ... *cough*");
		npc.addReply(Arrays.asList("blood", "vampirette_entrails", "bat_entrails"),
		        "I need blood. I can take it from the entrails of the alive and undead. I will mix the bloods together for you and #fill your #goblet, if you let me drink some too. But I'm afraid of the powerful #lord.");
		
		npc.addReply(Arrays.asList("lord", "vampire", "skull_ring"),
		        "The Vampire Lord rules these Catacombs! And I'm afraid of him. I can only help you if you kill him and bring me his skull ring with the #goblet.");

		npc.addReply(Arrays.asList("empty_goblet", "goblet"),
		        "Only a powerful talisman like this cauldron or a special goblet should contain blood.");

		Map<String, Integer> requiredResources = new HashMap<String, Integer>();
		requiredResources.put("vampirette_entrails", 7);
		requiredResources.put("bat_entrails", 7);
		requiredResources.put("skull_ring", 1);
		requiredResources.put("empty_goblet", 1);
		ProducerBehaviour behaviour = new ProducerBehaviour("sicky_fill_goblet",
				"fill", "goblet", requiredResources, 5 * 60, true);
		npc.addProducer(behaviour,
		        "Please don't try to kill me...I'm just a sick old #vampire. Do you have any #blood I could drink? If you have an #empty_goblet I will #fill it with blood for you in my cauldron.");

	}
}
