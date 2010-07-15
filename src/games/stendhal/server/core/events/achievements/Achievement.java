package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

public class Achievement {
	
	private final String identifier;
	
	private final String title;
	
	private final Category category;
	
	private final String description;
	
	private final ChatCondition condition;

	
	/**
	 * create a new achievement
	 * 
	 * @param identifier
	 * @param title
	 * @param category
	 */
	public Achievement(String identifier, String title, Category category, String description, ChatCondition condition) {
		this.identifier = identifier;
		this.title = title;
		this.category = category;
		this.condition = condition;
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}

	public boolean isFulfilled(Player p) {
		boolean fullfilled = condition.fire(p, null, null);
		return fullfilled;
	}

	@Override
	public String toString() {
		return "Achievement<id: "+identifier+", title: "+title+">";
	}

}
