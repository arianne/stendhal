package games.stendhal.server.maps.quests.piedpiper;


import java.util.LinkedList;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

public class TPPQuestHelperFunctions implements ITPPQuestConstants {
	
	private static LinkedList<Creature> rats = new LinkedList<Creature>();	
	
	public static SpeakerNPC getMainNPC() {
		return SingletonRepository.getNPCList().get("Mayor Chalmers");
	}
	
	/**
	 * function for calculating reward's moneys for player
	 *
	 * @param player
	 * 			- player which must be rewarded
	 * @return
	 * 			gold amount for hunting rats.
	 */
	public static int calculateReward(Player player) {
		int moneys = 0;
		int kills = 0;
		for(int i=0; i<RAT_TYPES.size(); i++) {
			try {
				final String killed = player.getQuest(QUEST_SLOT,i+1);
				// have player quest slot or not yet?
				if (killed != null) {
					kills=Integer.decode(killed);
				}
			} catch (NumberFormatException nfe) {
				// player's quest slot don't contain valid number
				// so he didn't killed such creatures.
			}
			moneys = moneys + kills*RAT_REWARDS.get(i);
		}
		return(moneys);
	}
	

	public void setRats(LinkedList<Creature> rats) {
		TPPQuestHelperFunctions.rats = rats;
	}

	public static LinkedList<Creature> getRats() {
		return rats;
	}
	
	/**
	 * 
	 */
	public static int getRatsCount() {
		return(getRats().size());
	}
}

