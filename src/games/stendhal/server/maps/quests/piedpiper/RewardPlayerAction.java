package games.stendhal.server.maps.quests.piedpiper;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 *  NPC's actions when player asks for his reward.
 *
 */
public class RewardPlayerAction implements ChatAction, ITPPQuestConstants {

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser mayor) {
		    	final int quantity = TPPQuestHelperFunctions.calculateReward(player);
		    	// to avoid giving karma without job
		    	if(quantity==0) {
		    		mayor.say("You didn't kill any rats which invaded the city, so you don't deserve a reward.");
		    		return;
		    	}
		    	player.addKarma(5);
		    	final StackableItem moneys = (StackableItem) SingletonRepository.getEntityManager()
		    				.getItem("money");
		    	moneys.setQuantity(quantity);
		    	player.equipOrPutOnGround(moneys);
		    	mayor.say("Please take "+quantity+" money, thank you very much for your help.");
		    	player.setQuest(QUEST_SLOT, "done");
			}
}
