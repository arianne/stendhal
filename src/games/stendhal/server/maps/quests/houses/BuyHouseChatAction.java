
package games.stendhal.server.maps.quests.houses;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.HouseKey;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

final class BuyHouseChatAction implements ChatAction {

//	private final String location;

	private int cost;

	/**
	 * Creates a new BuyHouseChatAction.
	 * 
	 * @param location
	 *            where are the houses?
	 */
	BuyHouseChatAction(final int cost) {
		this.cost = cost;
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {

		final int number = sentence.getNumeral().getAmount();
		// now check if the house they said is free
		final String itemName = Integer.toString(number);

		final HousePortal houseportal = HouseUtilities.getHousePortal(number);

		if (houseportal == null) {
			// something bad happened
			engine.say("Sorry I did not understand you, could you try saying the house number you want again please?");
			engine.setCurrentState(ConversationStates.QUEST_OFFERED);
			return;
		}

		final String owner = houseportal.getOwner();
		if (owner.length() == 0) {
			
			// it's available, so take money
			if (player.isEquipped("money", cost)) {
				final Item key = SingletonRepository.getEntityManager().getItem(
																				"house key");

				final String doorId = houseportal.getDoorId();

				final int locknumber = houseportal.getLockNumber();
				((HouseKey) key).setup(doorId, locknumber, player.getName());
			
				if (player.equipToInventoryOnly(key)) {
					engine.say("Congratulations, here is your key to " + doorId
							   + "! Make sure you change the locks if you ever lose it. Do you want to buy a spare key, at a price of "
							   + HouseBuyingMain.COST_OF_SPARE_KEY + " money?");
					
					player.drop("money", cost);
					// remember what house they own
					player.setQuest(HouseBuyingMain.QUEST_SLOT, itemName);

					// put nice things and a helpful note in the chest
					HouseBuyingMain.fillChest(HouseUtilities.findChest(houseportal));

					// set the time so that the taxman can start harassing the player
					final long time = System.currentTimeMillis();
					houseportal.setExpireTime(time);

					houseportal.setOwner(player.getName());
					engine.setCurrentState(ConversationStates.QUESTION_1);
				} else {
					engine.say("Sorry, you can't carry more keys!");
				}
			
			} else {
				engine.say("You do not have enough money to buy a house!");
			}
		
		} else {
			engine.say("Sorry, house " + itemName
					   + " is sold, please ask for a list of #unsold houses, or give me the number of another house.");
			engine.setCurrentState(ConversationStates.QUEST_OFFERED);
		}
	}
}
