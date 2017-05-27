package games.stendhal.server.events;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Events;
import games.stendhal.server.core.rp.achievement.Achievement;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

public class ReachedAchievementEvent extends RPEvent {

	private static final Logger logger = Logger.getLogger(ReachedAchievementEvent.class);

	public static void generateRPClass() {
		try {
			RPClass clazz = new RPClass(Events.REACHED_ACHIEVEMENT);
			clazz.addAttribute("category", Type.STRING);
			clazz.addAttribute("title", Type.STRING);
			clazz.addAttribute("description", Type.STRING);
		} catch (Exception e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public ReachedAchievementEvent(Achievement achievement) {
		super(Events.REACHED_ACHIEVEMENT);
		put("category", achievement.getCategory().toString());
		put("title", achievement.getTitle());
		put("description", achievement.getDescription());
	}

}
