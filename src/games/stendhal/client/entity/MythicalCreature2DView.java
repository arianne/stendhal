/*
 * @(#) games/stendhal/client/entity/MythicalCreature2DView.java
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
 * The 2D view of a mythical creature.
 */
public class MythicalCreature2DView extends Creature2DView {
	/**
	 * Create a 2D view of a mythical creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public MythicalCreature2DView(final MythicalCreature creature) {
		super(creature);
	}


	//
	// AnimatedEntity
	//

	@Override
	protected void buildAnimations(final RPObject object) {
		buildAnimations(object, 6.0, 8.0);
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
		return new Rectangle.Double(getX(), getY(), 6.0, 8.0);
	}
}
