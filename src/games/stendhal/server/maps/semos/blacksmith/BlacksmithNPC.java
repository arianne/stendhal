package games.stendhal.server.maps.semos.blacksmith;

import games.stendhal.server.entity.npc.ShopList;
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
public class BlacksmithNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(SpeakerNPC npc) {
		npc.addReply("wood",
		        "I need some wood to keep my furnace lit. You can find any amount of it just lying around in the forest.");

		npc.addReply(Arrays.asList("ore", "iron", "iron_ore"),
				"You can find iron ore up in the mountains west of Or'ril, near the dwarf mines. Be careful up there!");

		npc.addReply("gold_pan",
		        "With this tool you are able to prospect for gold. Along Or'ril river, south of the castle, is a lake near a waterfall. I once found a #gold_nugget there. Maybe you would be lucky too.");

		npc.addReply("gold_nugget",
		        "My brother Joshua lives in Ados. He can cast gold nuggets to bars of pure gold.");

		npc.addHelp("If you bring me #wood and #iron_ore, I can #cast the iron for you. Then you could sell it to the dwarves, to make yourself a little money.");
		npc.addJob("Greetings. Unfortunately, because of the war, I'm not currently allowed to sell weapons to anyone except the official armoury. However, I can still #cast iron for you, or I can make you an #offer on some good tools.");
		npc.addGoodbye();
		new SellerAdder().addSeller(npc, new SellerBehaviour(ShopList.get().get("selltools")));

		// Xoderos casts iron if you bring him wood and iron ore.
		Map<String, Integer> requiredResources = new TreeMap<String, Integer>();	// use sorted TreeMap instead of HashMap
		requiredResources.put("wood", 1);
		requiredResources.put("iron_ore", 1);

		ProducerBehaviour behaviour = new ProducerBehaviour("xoderos_cast_iron",
				"cast", "iron", requiredResources, 5 * 60);

		new ProducerAdder().addProducer(npc, behaviour,
		        "Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.");
	}
}
