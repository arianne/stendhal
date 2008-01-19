	package games.stendhal.server.maps.semos.city;

import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.village.SheepSellerNPC;

import java.util.HashMap;
import java.util.Map;

/**
 * A merchant (original name: Sato) who buys sheep from players.
 */
public class SheepBuyerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(SpeakerNPC npc) {
		class SheepBuyerBehaviour extends BuyerBehaviour {
			SheepBuyerBehaviour(Map<String, Integer> items) {
				super(items);
			}

			private int getValue(Sheep sheep) {
				return Math.round(getUnitPrice(chosenItemName) * ((float) sheep.getWeight() / (float) sheep.MAX_WEIGHT));
			}
			
			@Override
			public int getCharge(Player player) {
				if (player.hasSheep()) {
					Sheep sheep = player.getSheep();
					return getValue(sheep);
				} else {
					return 0;
				}
			}
			
			@Override
			public boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
				// amount is currently ignored.

				Sheep sheep = player.getSheep();

				if (sheep != null) {
					if (seller.squaredDistance(sheep) > 5 * 5) {
						seller.say("I can't see that sheep from here! Bring it over so I can assess it properly.");
					} else if (getValue(sheep) < SheepSellerNPC.BUYING_PRICE) {
						// prevent newbies from selling their sheep too early
						seller.say("Nah, that sheep looks too skinny. Feed it with red berries, and come back when it has become fatter.");
					} else {
						seller.say("Thanks! Here is your money.");
						payPlayer(player);
						player.removeSheep(sheep);

						player.notifyWorldAboutChanges();

						sheep.getZone().remove(sheep);
						return true;
					}
				} else {
					seller.say("You don't have any sheep, " + player.getTitle() + "! What are you trying to pull?");
				}

				return false;
			}
		}

		Map<String, Integer> buyitems = new HashMap<String, Integer>();
		buyitems.put("sheep", 150);

		npc.addGreeting();
		npc.addJob("I buy sheep here in Semos, then I send them up to Ados where they are exported.");
		npc.addHelp("I purchase sheep, at what I think is a fairly reasonable price. Just say if you want to #sell #sheep, and I will set up a deal!");
		new BuyerAdder().add(npc, new SheepBuyerBehaviour(buyitems), true);
		npc.addGoodbye();
	}
}
