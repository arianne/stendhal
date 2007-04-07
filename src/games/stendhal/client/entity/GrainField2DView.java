/*
 * @(#) games/stendhal/client/entity/GrainField2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * The 2D view of a grain field.
 */
public class GrainField2DView extends AnimatedEntity2DView {
	private GrainField	grainField;


	/**
	 * Create a 2D view of a grain field.
	 *
	 * @param	grainField	The entity to render.
	 */
	public GrainField2DView(final GrainField grainField) {
		super(grainField);

		this.grainField = grainField;
	}


	//
	// Entity2DView
	//

	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(
			getX() + grainField.getWidth() - 1.0,
			getY() + grainField.getHeight() - 1.0,
			1.0, 1.0);
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
	public int getZIndex() {
		return 3000;
	}
}
