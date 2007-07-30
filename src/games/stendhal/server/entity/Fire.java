package games.stendhal.server.entity;

import games.stendhal.server.events.UseListener;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

public class Fire extends Entity implements UseListener {
	public static void generateRPClass() {
		RPClass fire = new RPClass("fire");
		fire.isA("entity");
	}

	public Fire(int width, int height) {
		setDescription("You see a flickering light. You are tempted to touch it.");
		setRPClass("fire");
		put("type", "fire");

		setWidth(width);
		setHeight(height);

		setObstacle(true);
	}


	//
	// UseListener
	//

	public boolean onUsed(RPEntity user) {
		user.sendPrivateText("No good idea!");
		return true;
	}
}
