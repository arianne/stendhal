/*
 * @(#) games/stendhal/client/entity/HugeCreature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a huge creature.
 */
public class HugeCreature2DView extends Creature2DView {
	/**
	 * Create a 2D view of a huge creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public HugeCreature2DView(final HugeCreature creature) {
		super(creature);
	}


	//
	// AnimatedEntity
	//

	@Override
	protected void buildAnimations(final RPObject object) {
		buildAnimations(object, 3.0, 4.0);
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
		return new Rectangle.Double(getX(), getY(), 3.0, 4.0);
	}
}
