package games.stendhal.server.maps.nalwor.assassinhq;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

import java.util.HashMap;
import java.util.Map;

/**
 * Inside Nalwor Assassin Headquarters - cellar .
 */

public class ChiefFalatheenDishwasherNPC extends SpeakerNPCFactory {
	private ShopList shops = SingletonRepository.getShopList();
			@Override
			public void createDialog(SpeakerNPC dishwasher) {
				dishwasher.addGreeting("You better have a good excuse for bothering me. I'm up to my neck in dishwater!");
				dishwasher.addJob("It is my job to wash all the dishes for all these pesty little brats.");
				dishwasher.addHelp("I can buy your vegetables and herbs.  Please see blackboards on wall for what i need.");
				dishwasher.addOffer("Look at blackboards on wall to see my prices.");
				dishwasher.addQuest("You could try to help me #escape from these hoodlums. Well... maybe not.");
				dishwasher.addGoodbye("Don't forget where I am now. Come back and see me some time. I do get lonely.");
				dishwasher.addReply("escape", "Yes! I want to pursue my dream. Mother Helena offered me a most wonderful job.  She needs a dishwasher. Lots of complaining customers!!!");

				Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("shuriken", 20);
				offerings.put("amulet", 800);
				offerings.put("black pearl", 100);
				offerings.put("lucky charm", 60);
				offerings.put("knife", 5);
				offerings.put("dagger", 20);
				offerings.put("skull ring", 250);
				offerings.put("greater antidote", 80);
				new BuyerAdder().add(dishwasher, new BuyerBehaviour(shops.get("buyveggiesandherbs")), true);			    
			    
			   	}
}
