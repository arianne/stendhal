package games.stendhal.server.core.events.achievements;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ObtainAchievementsFactory extends AchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.OBTAIN;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> achievements = new LinkedList<Achievement>();
		return achievements;
	}

}
