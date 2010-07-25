package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
/**
 * An Achievement a player can reach while playing the game.
 * Achievements are given for example for doing a certain number of quests or killing a number of special creatures
 *  
 * @author madmetzger
 */
public class Achievement {
	
	public static final int EASY_BASE_SCORE = 10;
	
	public static final int MEDIUM_BASE_SCORE = 50;
	
	public static final int HARD_BASE_SCORE = 1000;
	
	private final String identifier;
	
	private final String title;
	
	private final Category category;
	
	private final String description;
	
	private final int baseScore;
	
	private final ChatCondition condition;

	
	/**
	 * create a new achievement
	 * 
	 * @param identifier
	 * @param title
	 * @param category
	 * @param condition
	 */
	public Achievement(String identifier, String title, Category category, String description, int baseScore, ChatCondition condition) {
		this.identifier = identifier;
		this.title = title;
		this.category = category;
		this.condition = condition;
		this.description = description;
		this.baseScore = baseScore;
	}

	/**
	 * @return the category of this achievement
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the identifying string
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the title a player gets awarded for this achievement
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the description of what to do to get this achievement
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return the base score for this achievement
	 */
	public int getBaseScore() {
		return this.baseScore;
	}

	/**
	 * Check if a player has fullfilled this achievement
	 * @param p the player to check
	 * @return true iff this achievement's condtion evalutates to true
	 */
	public boolean isFulfilled(Player p) {
		boolean fullfilled = condition.fire(p, null, null);
		return fullfilled;
	}

	@Override
	public String toString() {
		return "Achievement<id: "+identifier+", title: "+title+">";
	}

}
