/*
 * @(#) games/stendhal/client/entity/BigCreature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * The 2D view of a big creature.
 */
public class BigCreature2DView extends Creature2DView {
	/**
	 * Create a 2D view of a big creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public BigCreature2DView(final BigCreature creature) {
		super(creature);
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
		return new Rectangle.Double(getX(), getY(), 2.0, 2.0);
	}
}
