package games.stendhal.client.entity;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 *
 */
public class Fire extends AnimatedStateEntity {

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		list.clear();
	}

	@Override
	public ActionType defaultAction() {
		return null;
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new Fire2DView(this);
	}
}
