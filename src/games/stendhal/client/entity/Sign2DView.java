/*
 * @(#) games/stendhal/client/entity/Sign2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import marauroa.common.game.RPObject;

import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a sign.
 */
public class Sign2DView extends Entity2DView {
	/**
	 * Create a 2D view of a sign.
	 *
	 * @param	entity		The entity to render.
	 */
	public Sign2DView(final Sign sign) {
		super(sign);
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 *
	 *
	 */
	@Override
	protected void buildRepresentation(final RPObject object) {
		String name;


		if (object.has("class")) {
			name = object.get("class");
		} else {
			name = "default";
		}

		sprite = SpriteStore.get().getSprite("data/sprites/signs/" + name + ".png");
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
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
		return 5000;
	}
}
