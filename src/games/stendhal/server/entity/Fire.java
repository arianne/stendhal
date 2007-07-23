package games.stendhal.server.entity;

import games.stendhal.server.events.UseListener;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

public class Fire extends Entity implements UseListener {
	/**
	 * The entity width.
	 */
	private int width;

	/**
	 * The entity height.
	 */
	private int height;

	public static void generateRPClass() {
		RPClass fire = new RPClass("fire");
		fire.isA("entity");
		fire.addAttribute("width", Type.SHORT);
		fire.addAttribute("height", Type.SHORT);
	}

	public Fire(int width, int height) {
		setDescription("You see a flickering light. You are tempted to touch it.");
		setRPClass("fire");
		put("type", "fire");
		put("width", width);
		put("height", height);
		this.width = width;
		this.height = height;
	}

	/**
	 * Get the entity height.
	 *
	 * @return The height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the entity width.
	 *
	 * @return The width.
	 */
	public int getWidth() {
		return width;
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, getWidth(), getHeight());
	}

	@Override
	public boolean isObstacle(Entity entity) {
		return true;
	}

	//
	// UseListener
	//

	public boolean onUsed(RPEntity user) {
		user.sendPrivateText("No good idea!");
		return true;
	}
}
