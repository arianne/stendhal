package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.PlayerHasCompletedAchievementsCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * Factory for meta achievements
 * 
 * @author madmetzger
 */
public class MetaAchievementFactory extends AchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.META;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> achievements = new LinkedList<Achievement>();
		achievements.add(createAchievement("meta.quest.daily-weekly", "Conscientuous Comrade", 
										   "Complete all achievements for daily item quest, daily monster quest and weekly item quest",
										   Achievement.HARD_BASE_SCORE, new PlayerHasCompletedAchievementsCondition("quest.special.diq.500", "quest.special.wiq.5", "quest.special.dmq.500")));
		return achievements;
	}

}
