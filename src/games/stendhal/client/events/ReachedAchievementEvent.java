package games.stendhal.client.events;

import games.stendhal.client.entity.RPEntity;
/**
 * Handling reaching an achievement client side
 * 
 * @author madmetzger
 */
public class ReachedAchievementEvent extends Event<RPEntity>{

	@Override
	public void execute() {
		String achievementTitle = event.get("title");
		String achievementDescription = event.get("description");
		String achievementCategory = event.get("category");
		entity.onReachAchievement(achievementTitle, achievementDescription, achievementCategory);
	}

}
