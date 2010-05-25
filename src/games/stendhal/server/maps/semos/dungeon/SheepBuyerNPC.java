package games.stendhal.server.maps.semos.dungeon;

import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * An orcish NPC who buys sheep from players.
 * You get the weight of the sheep * 15 in gold coins.
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class SheepBuyerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		// TODO: The code is similar to Sato's SheepBuyerBehaviour.
		// Only the phrasing is different, and Sato doesn't buy
		// skinny sheep. Get rid of the code duplication.
		class SheepBuyerBehaviour extends BuyerBehaviour {
			SheepBuyerBehaviour(final Map<String, Integer> items) {
				super(items);
			}

			@Override
			public int getCharge(final SpeakerNPC npc, final Player player) {
				if (player.hasSheep()) {
					final Sheep sheep = player.getSheep();
					return Math.round(getUnitPrice(chosenItemName) * ((float) sheep.getWeight() / (float) sheep.MAX_WEIGHT));
				} else {
					return 0;
				}
			}

			@Override
			public boolean transactAgreedDeal(final SpeakerNPC seller, final Player player) {
				// amount is currently ignored.
				final Sheep sheep = player.getSheep();

				if (sheep != null) {
					if (seller.squaredDistance(sheep) > 5 * 5) {
						seller.say("*drool* Sheep flesh! Bring da sheep here!");
					} else {
						seller.say("Mmm... Is look yummy! Here, you take dis gold!");
						payPlayer(seller, player);

						player.removeSheep(sheep);
						player.notifyWorldAboutChanges();

						sheep.getZone().remove(sheep);

						return true;
					}
				} else {
					seller.say("Whut? Is not unnerstand... Maybe I hit you until you make sense!");
				}
				return false;
			}
		}

		final Map<String, Integer> buyitems = new HashMap<String, Integer>();
		buyitems.put("sheep", 1500);

		npc.addGreeting();
		npc.addJob(npc.getName() + " is buy real cheep from hoomans.");
		npc.addHelp(npc.getName() + " buy sheep! Sell me sheep! "
				+ npc.getName()	+ " is hungry!");
		new BuyerAdder().add(npc, new SheepBuyerBehaviour(buyitems), true);
		npc.addGoodbye();
	}
}
