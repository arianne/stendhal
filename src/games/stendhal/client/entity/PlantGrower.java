package games.stendhal.client.entity;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class PlantGrower extends Entity {
	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
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
		return new PlantGrower2DView(this);
	}
}
