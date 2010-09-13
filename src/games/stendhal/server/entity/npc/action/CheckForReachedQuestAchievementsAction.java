package games.stendhal.server.entity.npc.action;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
/**
 * Triggers checking for new reached quest achievements
 *  
 * @author madmetzger
 */
public class CheckForReachedQuestAchievementsAction implements ChatAction {

	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		SingletonRepository.getAchievementNotifier().onFinishQuest(player);
	}

}
