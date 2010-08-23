package games.stendhal.server.maps.semos.blacksmith;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * The blacksmith (original name: Xoderos). Brother of the goldsmith in Ados.
 * He refuses to sell weapons, but he casts iron for the player, and he sells
 * tools.
 * 
 * @author daniel
 * 
 * @see games.stendhal.server.maps.quests.HungryJoshua
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class BlacksmithNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addReply("wood",
		        "I need some wood to keep my furnace lit. You can find any amount of it just lying around in the forest.");

		npc.addReply(Arrays.asList("ore", "iron", "iron ore"),
				"You can find iron ore up in the mountains west of Or'ril, near the dwarf mines. Be careful up there!");

		npc.addReply("gold pan",
		        "With this tool you are able to prospect for gold. Along Or'ril river, south of the castle, is a lake near a waterfall. I once found a #'gold nugget' there. Maybe you would be lucky too.");

		npc.addReply("gold nugget",
		        "My brother Joshua lives in Ados. He can cast gold nuggets to bars of pure gold.");

		npc.addReply("bobbin", "I do #trade in tools but I don't have any bobbins, sorry. They are too fiddly for me to make. Try a dwarf.");
		npc.addReply(Arrays.asList("oil", "can of oil"), "Oh, fishermen supply us with that.");

		npc.addHelp("If you bring me #wood and #'iron ore', I can #cast the iron for you. Then you could sell it to the dwarves, to make yourself a little money.");
		npc.addJob("I am a blacksmith. I #cast iron, and #trade tools.");
		npc.addGoodbye();
		new SellerAdder().addSeller(npc, new SellerBehaviour(SingletonRepository.getShopList().get("selltools")));

		// Xoderos casts iron if you bring him wood and iron ore.
		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();	
		requiredResources.put("wood", 1);
		requiredResources.put("iron ore", 1);

		final ProducerBehaviour behaviour = new ProducerBehaviour("xoderos_cast_iron",
				"cast", "iron", requiredResources, 5 * 60);

		new ProducerAdder().addProducer(npc, behaviour,
		        "Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.");
		
		npc.setDescription("You see Xoderos, the strong Semos blacksmith.");
	}
}
