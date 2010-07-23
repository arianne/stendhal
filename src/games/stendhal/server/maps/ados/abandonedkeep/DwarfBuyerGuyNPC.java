package games.stendhal.server.maps.ados.abandonedkeep;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

import java.util.HashMap;
import java.util.Map;

/**
 * Inside Ados Abandoned Keep - level -3 .
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class DwarfBuyerGuyNPC extends SpeakerNPCFactory {
			@Override
			public void createDialog(final SpeakerNPC dwarfguy) {
				dwarfguy.addGreeting("What do you want?");
				dwarfguy.addJob("I buy odds and ends. Somebody has to do it.");
				dwarfguy.addHelp("Look at me! I am reduced to buying trinkets! How can I help YOU?");
				dwarfguy.addOffer("Don't bother me unless you have something I want! Check the blackboard for prices.");
				dwarfguy.addQuest("Unless you want to #buy this place, you cannot do anything for me.");
				dwarfguy.addGoodbye("Be off with you!");
			    dwarfguy.addReply("buy", "What? Why you couldn't even begin to come up with enough money for that!");
			    dwarfguy.addReply("YOU", "Yes, I am talking to YOU! Who else would I be talking to!");

				final Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("shuriken", 20);
				offerings.put("amulet", 800);
				offerings.put("black pearl", 100);
				offerings.put("lucky charm", 60);
				offerings.put("knife", 5);
				offerings.put("dagger", 20);
				offerings.put("skull ring", 250);
				offerings.put("greater antidote", 80);
				offerings.put("marbles", 80);
				offerings.put("magical needle", 1000);
				offerings.put("snowglobe", 150);
				offerings.put("silk gland", 500);
				new BuyerAdder().add(dwarfguy, new BuyerBehaviour(offerings), false);			    
			    
			   	}
}
