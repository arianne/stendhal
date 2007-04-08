/*
 * @(#) games/stendhal/client/entity/NormalCreature2DView.java
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
 * The 2D view of a normal creature.
 */
public class NormalCreature2DView extends Creature2DView {
	/**
	 * Create a 2D view of a normal creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public NormalCreature2DView(final NormalCreature creature) {
		super(creature);
	}


	//
	// AnimatedEntity
	//

	@Override
	protected void buildAnimations(final RPObject object) {
		buildAnimations(object, 1.5, 2.0);
	}
}
