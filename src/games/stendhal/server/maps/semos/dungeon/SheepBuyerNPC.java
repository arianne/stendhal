package games.stendhal.server.maps.semos.dungeon;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * An orcish NPC who buys sheep from players.
 * You get the weight of the sheep * 50 in gold coins.
 */
public class SheepBuyerNPC extends SpeakerNPCFactory {

	@Override
	protected void createDialog(SpeakerNPC npc) {
		// TODO: The code is similar to Sato's SheepBuyerBehaviour.
		// Only the phrasing is different, and Sato doesn't buy
		// skinny sheep. Get rid of the code duplication.
		class SheepBuyerBehaviour extends BuyerBehaviour {
			SheepBuyerBehaviour(Map<String, Integer> items) {
				super(items);
			}

			@Override
			public int getCharge(Player player) {
				if (player.hasSheep()) {
					Sheep sheep = player.getSheep();
					return Math.round(getUnitPrice(chosenItem) * ((float) sheep.getWeight() / (float) sheep.MAX_WEIGHT));
				} else {
					return 0;
				}
			}

			@Override
			public boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
				// amount is currently ignored.
				if (player.hasSheep()) {
					Sheep sheep = player.getSheep();
					if (seller.squaredDistance(sheep) > 5 * 5) {
						seller.say("*drool* Sheep flesh! Bring da sheep here!");
					} else {
						seller.say("Mmm... Is look yummy! Here, you take dis gold!");
						payPlayer(player);

						StendhalRPRuleProcessor.get().removeNPC(sheep);
						StendhalRPWorld.get().remove(sheep.getID());
						player.removeSheep(sheep);

						player.notifyWorldAboutChanges();
						return true;
					}
				} else {
					seller.say("Whut? Is not unnerstand... Maybe I hit you until you make sense!");
				}
				return false;
			}
		}

		Map<String, Integer> buyitems = new HashMap<String, Integer>();
		buyitems.put("sheep", 1500);

		npc.addGreeting();
		npc.addJob(npc.getName() + " is buy real cheep from hoomans.");
		npc.addHelp(npc.getName() + " buy sheep! Sell me sheep! "
				+ npc.getName()	+ " is hungry!");
		npc.addBuyer(new SheepBuyerBehaviour(buyitems));
		npc.addGoodbye();
	}
}
