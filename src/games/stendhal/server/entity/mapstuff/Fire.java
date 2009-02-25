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
		final RPClass fire = new RPClass("fire");
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
	public Fire(final int width, final int height) {
		setDescription("You see a flickering light. You are tempted to touch it.");
		setRPClass("fire");
		put("type", "fire");

		setSize(width, height);
		setResistance(100);
	}

	public boolean onUsed(final RPEntity user) {
		user.sendPrivateText("Not a good idea!");
		return true;
	}
}
