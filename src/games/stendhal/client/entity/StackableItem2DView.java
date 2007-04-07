/*
 * @(#) games/stendhal/client/entity/StackableItem2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a stackable item.
 */
public class StackableItem2DView extends Item2DView {
	/**
	 * The entity this view is for.
	 */
	private StackableItem	item;

	/**
	 * The image of the quantity.
	 */
	private Sprite		quantitySprite;


	/**
	 * Create a 2D view of a stackable item.
	 *
	 * @param	item		The entity to render.
	 */
	public StackableItem2DView(final StackableItem item) {
		super(item);

		this.item = item;
		quantitySprite = getQuantitySprite();
	}


	//
	// StackableItem2DView
	//

	/**
	 * Get the approriete quantity sprite.
	 *
	 *
	 */
	protected Sprite getQuantitySprite() {
		int	quantity;


		quantity = item.getQuantity();

		if (quantity == 1) {
			return null;
		} else {
			return GameScreen.get().createString(
				Integer.toString(quantity), Color.white);
		}
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
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
	}


	//
	// <EntityView>
	//

	/**
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	@Override
	public void draw(final GameScreen screen) {
		super.draw(screen);

		if (quantitySprite != null) {
			screen.draw(quantitySprite, getX(), getY());
		}
	}


	/**
	 * Update representation.
	 */
	@Override
	public void update() {
		quantitySprite = getQuantitySprite();
	}
}
