package games.stendhal.server.maps.nalwor.basement;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

import java.util.HashMap;
import java.util.Map;

/**
 * Inside Nalwor Inn basement .
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class ArcheryDealerNPC extends SpeakerNPCFactory {
			@Override
			public void createDialog(final SpeakerNPC magearcher) {
				magearcher.addGreeting("Well met, kind stranger.");
				magearcher.addJob("I buy archery equipment for our village.");
				magearcher.addHelp("I can offer you no help. Sorry.");
				magearcher.addOffer("Check the blackboard for prices.");
				magearcher.addQuest("I have no quest for you.");
				magearcher.addGoodbye("Have a happy. Bye.");
				//TODO: put into shop list?
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("crossbow", 400);
				offerings.put("wooden arrow", 1);
				offerings.put("steel arrow", 5);
				offerings.put("golden arrow", 20);
				offerings.put("power arrow", 50);
				offerings.put("wooden bow", 250);
				offerings.put("longbow", 300);
				offerings.put("composite bow", 350);
				offerings.put("hunter crossbow", 800);
				offerings.put("mithril bow", 2000);
				new BuyerAdder().add(magearcher, new BuyerBehaviour(offerings), true);			    
			    
			   	}
}
