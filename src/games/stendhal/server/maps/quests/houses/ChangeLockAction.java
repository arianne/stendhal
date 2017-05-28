/**
 *
 */
package games.stendhal.server.maps.quests.houses;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.HouseKey;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

final class ChangeLockAction extends HouseChatAction implements ChatAction {
	private static final Logger logger = Logger.getLogger(ChangeLockAction.class);

	protected ChangeLockAction(final String questslot) {
		super(questslot);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (player.isEquipped("money", HouseChatAction.COST_OF_SPARE_KEY)) {
			// we need to find out which this houseportal is so we can change lock
			final String claimedHouse = player.getQuest(questslot);

			try {
				final int id = Integer.parseInt(claimedHouse);
				final HousePortal portal = HouseUtilities.getHousePortal(id);
				// change the lock
				portal.changeLock();
				// make a new key for the player, with the new locknumber
				final String doorId = portal.getDoorId();
				final Item key = SingletonRepository.getEntityManager().getItem("house key");
				final int locknumber = portal.getLockNumber();

				((HouseKey) key).setup(doorId, locknumber, player.getName());
				if (player.equipToInventoryOnly(key)) {
					player.drop("money", HouseChatAction.COST_OF_SPARE_KEY);
					raiser.say("The locks have been changed for " + doorId + ", here is your new key. Do you want to buy a spare key, at a price of "
							   + HouseChatAction.COST_OF_SPARE_KEY + " money?");
					raiser.setCurrentState(ConversationStates.QUESTION_1);
				} else {
					// if the player doesn't have the space for the key, change the locks anyway as a security measure, but don't charge.
					raiser.say("The locks have been changed for "
							   + doorId + ", but you do not have space to carry the new key. I haven't charged you for this service. "
							   + "If you want to go away and make space, come back and I will offer you the chance to buy a spare key. Goodbye.");
					raiser.setCurrentState(ConversationStates.IDLE);
				}
			} catch (final NumberFormatException e) {
				logger.error("Invalid number in house slot", e);
				raiser.say("Sorry, something bad happened. I'm terribly embarassed.");
				return;
			}
		} else {
			raiser.say("You need to pay " + HouseChatAction.COST_OF_SPARE_KEY + " money to change the lock and get a new key for your house.");
		}
	}
}
