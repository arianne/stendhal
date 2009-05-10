/**
 * 
 */
package games.stendhal.server.maps.quests.houses;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

final class ResellHouseAction implements ChatAction {

	private int cost;

	ResellHouseAction(final int cost) {
		this.cost = cost;
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {

		// we need to find out where this house is so we know how much to refund them
		final String claimedHouse = player.getQuest(HouseBuyingMain.QUEST_SLOT);
	
		try {
			
			final int id = Integer.parseInt(claimedHouse);
			final HousePortal portal = HouseUtilities.getHousePortal(id);
			
			final int refund = (cost * HouseBuyingMain.DEPRECIATION_PERCENTAGE) / 100 - HouseBuyingMain.houseTax.getTaxDebt(portal);

			final StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
			money.setQuantity(refund);
			player.equipOrPutOnGround(money);
	
			portal.changeLock();
			portal.setOwner("");
			// the player has sold the house. clear the slot
			player.removeQuest(HouseBuyingMain.QUEST_SLOT);
			engine.say("Thanks, here is your " + Integer.toString(refund)
					   + " money owed, from the house value, minus any owed taxes. Now that you don't own a house "
					   + "you would be free to buy another if you want to.");
		} catch (final NumberFormatException e) {
			HouseBuyingMain.logger.error("Invalid number in house slot", e);
			engine.say("Sorry, something bad happened. I'm terribly embarassed.");
			return;
		}
	}
}
