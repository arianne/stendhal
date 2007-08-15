package games.stendhal.server.entity;

import games.stendhal.server.events.UseListener;

import marauroa.common.game.RPClass;

/**
 * Burning fire
 */
public class Fire extends Entity implements UseListener {
	public static void generateRPClass() {
		RPClass fire = new RPClass("fire");
		fire.isA("entity");
	}

	/**
	 * creates a new fiew
	 *
	 * @param width width in grid units
	 * @param height height in grid units
	 */
	public Fire(int width, int height) {
		setDescription("You see a flickering light. You are tempted to touch it.");
		setRPClass("fire");
		put("type", "fire");

		setWidth(width);
		setHeight(height);

		setObstacle(true);
	}

	public boolean onUsed(RPEntity user) {
		user.sendPrivateText("Not a good idea!");
		return true;
	}
}
