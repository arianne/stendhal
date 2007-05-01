/*
 * @(#) games/stendhal/client/entity/Item2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import games.stendhal.client.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

/**
 * The 2D view of an item.
 */
public class Spell2DView extends Entity2DView {
	/**
	 * Create a 2D view of an item.
	 *
	 * @param	entity		The entity to render.
	 */
	public Spell2DView(final Spell spell) {
		super(spell);
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 *
	 * @param	object		An entity object.
	 */
	@Override
	protected void buildRepresentation(final RPObject object) {
		String name = getEntity().getEntityClass();
		String subclass = getEntity().getEntitySubClass();

		if(subclass != null) {
			name += "/" + subclass;
		}

		sprite = SpriteStore.get().getSprite(
			"data/sprites/spells/" + name + ".png");
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
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
		return 7000;
	}
}
