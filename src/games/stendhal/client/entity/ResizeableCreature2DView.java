/*
 * @(#) games/stendhal/client/entity/ResizeableCreature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * The 2D view of a resizable creature.
 */
public class ResizeableCreature2DView extends Creature2DView {
	private ResizeableCreature	creature;


	/**
	 * Create a 2D view of a resizable creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public ResizeableCreature2DView(final ResizeableCreature creature) {
		super(creature);

		this.creature = creature;
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
		return new Rectangle.Double(getX(), getY(), creature.getWidth(), creature.getHeight());
	}
}
