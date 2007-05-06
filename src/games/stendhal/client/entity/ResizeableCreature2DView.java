/*
 * @(#) games/stendhal/client/entity/ResizeableCreature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;


/**
 * The 2D view of a resizable creature.
 */
public class ResizeableCreature2DView extends Creature2DView {
	/**
	 * Create a 2D view of a resizable creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public ResizeableCreature2DView(final ResizeableCreature creature) {
		super(creature);
	}


	//
	// Creature2DView
	//

	/**
	 * Set the appropriete drawn size based on the creature.
	 * <strong>NOTE: This is called from the constructor.</strong>
	 */
	@Override
	protected void updateSize() {
		width = entity.getWidth();
		height = entity.getHeight();

		// Hack for human like creatures
		if ((Math.abs(width - 1.0) < 0.1) && (Math.abs(height - 2.0) < 0.1)) {
			width = 1.5;
			height = 2.0;
		}
	}


	//
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(Map<String, AnimatedSprite> map) {
		buildSprites(map, width, height);
	}


	//
	// <EntityView>
	//

	/**
	 * Update representation.
	 */
	@Override
	protected void update() {
		buildRepresentation();

		super.update();
	}
}
