	package games.stendhal.server.maps.semos.city;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
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
	public void createDialog(final SpeakerNPC npc) {
		class SheepBuyerBehaviour extends BuyerBehaviour {
			SheepBuyerBehaviour(final Map<String, Integer> items) {
				super(items);
			}

			private int getValue(final Sheep sheep) {
				return Math.round(getUnitPrice(chosenItemName) * ((float) sheep.getWeight() / (float) sheep.MAX_WEIGHT));
			}

			@Override
			public int getCharge(final SpeakerNPC seller, final Player player) {
				if (player.hasSheep()) {
					final Sheep sheep = player.getSheep();
					return getValue(sheep);
				} else {
					seller.say("You don't have any sheep, " + player.getTitle() + "! What are you trying to pull?");
					return 0;
				}
			}
			
			/**
			 * Move a bought sheep to the den if there's space, or remove it 
			 * from the zone otherwise.
			 * 
			 * @param sheep the sheep to be moved
			 */
			private void moveSheep(Sheep sheep) {
				// The area of the sheed den.
				int x = Rand.randUniform(39, 54);
				int y = Rand.randUniform(24, 29);
				StendhalRPZone zone = sheep.getZone();
				if (!StendhalRPAction.placeat(zone, sheep, x, y)) {
					// there was no room for the sheep. Simply eat it
					sheep.getZone().remove(sheep);  
				}
			}

			@Override
			public boolean transactAgreedDeal(final SpeakerNPC seller, final Player player) {
				// amount is currently ignored.

				final Sheep sheep = player.getSheep();

				if (sheep != null) {
					if (seller.squaredDistance(sheep) > 5 * 5) {
						seller.say("I can't see that sheep from here! Bring it over so I can assess it properly.");
					} else if (getValue(sheep) < SheepSellerNPC.BUYING_PRICE) {
						// prevent newbies from selling their sheep too early
						seller.say("Nah, that sheep looks too skinny. Feed it with red berries, and come back when it has become fatter.");
					} else {
						seller.say("Thanks! Here is your money.");
						payPlayer(seller, player);
						player.removeSheep(sheep);

						player.notifyWorldAboutChanges();
						moveSheep(sheep);

						return true;
					}
				} else {
					seller.say("You don't have any sheep, " + player.getTitle() + "! What are you trying to pull?");
				}

				return false;
			}
		}

		final Map<String, Integer> buyitems = new HashMap<String, Integer>();
		buyitems.put("sheep", 150);

		npc.addGreeting();
		npc.addJob("I buy sheep here in Semos, then I send them up to Ados where they are exported.");
		npc.addHelp("I purchase sheep, at what I think is a fairly reasonable price. Just say if you want to #sell #sheep, and I will set up a deal!");
		new BuyerAdder().add(npc, new SheepBuyerBehaviour(buyitems), true);
		npc.addGoodbye();
	}
}
