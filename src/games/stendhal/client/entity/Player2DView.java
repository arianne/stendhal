/*
 * @(#) games/stendhal/client/entity/Player2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * The 2D view of an player.
 */
public class Player2DView extends RPEntity2DView {
	/**
	 * Create a 2D view of an player.
	 *
	 * @param	entity		The entity to render.
	 */
	public Player2DView(final Player entity) {
		super(entity);
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
		return new Rectangle.Double(getX(), getY(), 1.0, 2.0);
	}
}
