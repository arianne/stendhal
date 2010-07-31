package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.PlayerHasKilledNumberOfCreaturesCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * Factory for fighting achievements
 *  
 * @author madmetzger
 */
public class FightingAchievementFactory extends AchievementFactory {
	
	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> fightingAchievements = new LinkedList<Achievement>();
		fightingAchievements.add(createAchievement("fight.general.rats", "Rat Hunter", "Kill 15 rats", Achievement.EASY_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition("rat", 15)));
		fightingAchievements.add(createAchievement("fight.general.exterminator", "Exterminator", "Kill 10 rats of each kind", Achievement.MEDIUM_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition(10, "rat", "caverat", "venomrat", "zombie rat", "venom rat", "giantrat", "ratman", "ratwoman", "archrat")));
		return fightingAchievements;
	}

	@Override
	protected Category getCategory() {
		return Category.FIGHTING;
	}

}
