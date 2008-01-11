package games.stendhal.server.entity.mapstuff;

import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

import marauroa.common.game.RPClass;

/**
 * Burning fire.
 */
public class Fire extends Entity implements UseListener {
	public static void generateRPClass() {
		RPClass fire = new RPClass("fire");
		fire.isA("entity");
	}

	/**
	 * creates a new fire.
	 * 
	 * @param width
	 *            width in grid units
	 * @param height
	 *            height in grid units
	 */
	public Fire(int width, int height) {
		setDescription("You see a flickering light. You are tempted to touch it.");
		setRPClass("fire");
		put("type", "fire");

		setSize(width, height);
		setResistance(100);
	}

	public boolean onUsed(RPEntity user) {
		user.sendPrivateText("Not a good idea!");
		return true;
	}
}
