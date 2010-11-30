package games.stendhal.server.core.rp.achievement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ObtainAchievementsFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.OBTAIN;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> achievements = new LinkedList<Achievement>();
		//achievements.add(createAchievement("obtain.wish", "A wish came true", "Get an item from the wishing well", Achievement.EASY_BASE_SCORE, null));
		return achievements;
	}

}
