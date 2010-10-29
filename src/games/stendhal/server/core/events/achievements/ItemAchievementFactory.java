package games.stendhal.server.core.events.achievements;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ItemAchievementFactory extends AchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.ITEM;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> itemAchievements = new LinkedList<Achievement>();
		return itemAchievements;
	}

}
