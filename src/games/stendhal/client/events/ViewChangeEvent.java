package games.stendhal.client.events;

import games.stendhal.client.GameScreen;
import games.stendhal.client.entity.Entity;

/**
 * View center changing event.
 */
public class ViewChangeEvent extends Event<Entity> {
	@Override
	public void execute() {
		int x = event.getInt("x");
		int y = event.getInt("y");
		
		GameScreen.get().positionChanged(x, y);
	}
}
