package games.stendhal.server.entity.mapstuff.useable;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.events.ViewChangeEvent;

/**
 * An entity that when used, tells the client to change the view center.
 */
public class ViewChangeEntity extends UseableEntity {
	private final int x;
	private final int y;
	
	/**
	 * Create a new ViewChangeEntity.
	 * 
	 * @param x x coordinate of the view center 
	 * @param y y coordinate of the view center
	 */
	public ViewChangeEntity(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean onUsed(RPEntity user) {
		if (!nextTo(user)) {
			user.sendPrivateText("You cannot reach that from here.");
			return false;
		}
		user.addEvent(new ViewChangeEvent(x, y));

		return true;
	}
}
