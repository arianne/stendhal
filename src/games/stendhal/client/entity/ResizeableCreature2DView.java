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

import marauroa.common.game.RPObject;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

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
		super(creature, creature.getWidth(), creature.getHeight());

		this.creature = creature;
	}


	//
	// Creature2DView
	//

	/**
	 * Get the height.
	 *
	 * @return	The height in tile units.
	 */
	@Override
	public double getHeight() {
		return creature.getHeight();
	}


	/**
	 * Get the width.
	 *
	 * @return	The width in tile units.
	 */
	@Override
	public double getWidth() {
		return creature.getWidth();
	}
}
