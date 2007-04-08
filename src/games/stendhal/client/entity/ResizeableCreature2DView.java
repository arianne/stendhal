/*
 * @(#) games/stendhal/client/entity/ResizeableCreature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.Sprite;

import java.util.Map;

import marauroa.common.game.RPObject;

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


	//
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state animations.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load animations for.
	 */
	public void buildAnimations(Map<String, Sprite []> map, RPObject object) {
		double	width;
		double	height;
		double	drawWidth;
		double	drawHeight;


		width = getWidth();
		height = getHeight();

		// Hack for human like creatures

		if ((Math.abs(width - 1.0) < 0.1) && (Math.abs(height - 2.0) < 0.1)) {
			drawWidth = 1.5;
			drawHeight = 2.0;
		} else {
			drawWidth = width;
			drawHeight = height;
		}

		buildAnimations(map, object, drawWidth, drawHeight);
	}
}
