/*
 * @(#) games/stendhal/client/entity/PlantGrower2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * The 2D view of a plant grower.
 */
public class PlantGrower2DView extends Entity2DView {
	/**
	 * Create a 2D view of a plant grower.
	 *
	 * @param	entity		The entity to render.
	 */
	public PlantGrower2DView(final PlantGrower plantGrower) {
		super(plantGrower);
	}


	//
	// Entity2DView
	//

	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
        }

	/**
	 * Determines on top of which other entities this entity should be
	 * drawn. Entities with a high Z index will be drawn on top of ones
	 * with a lower Z index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return	The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 3000;
	}
}
