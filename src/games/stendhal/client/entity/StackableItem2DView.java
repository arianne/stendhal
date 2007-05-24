/*
 * @(#) games/stendhal/client/entity/StackableItem2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * The 2D view of a stackable item.
 */
public class StackableItem2DView extends Item2DView {
	/**
	 * The entity this view is for.
	 */
	private StackableItem	item;

	/**
	 * The quantity value changed.
	 */
	protected boolean	quantityChanged;

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
		quantityChanged = false;
	}


	//
	// StackableItem2DView
	//

	/**
	 * Get the appropriete quantity sprite.
	 *
	 * @return	A sprite representing the quantity,
	 *		or <code>null</code> for none.
	 */
	protected Sprite getQuantitySprite() {
		int	quantity;
		String	label;


		quantity = item.getQuantity();

		if (quantity <= 1) {
			return null;
		} else if(isContained() && (quantity > 99999999)) {
			label = (quantity / 1000000) + "M";
		} else if(isContained() && (quantity > 99999)) {
			label = (quantity / 1000) + "K";
		} else {
			label = Integer.toString(quantity);
		}

		return GameScreen.get().createString(label, Color.white);
	}


	//
	// Entity2DView
	//

	/**
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	@Override
	protected void draw(final GameScreen screen, Graphics2D g2d, int x, int y, int width, int height) {
		super.draw(screen, g2d, x, y, width, height);

		if (quantitySprite != null) {
			quantitySprite.draw(g2d, x, y);
		}
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
	 * Update representation.
	 */
	@Override
	public void update() {
		super.update();

		if(quantityChanged) {
			quantitySprite = getQuantitySprite();
			quantityChanged = false;
		}
	}


	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 *
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(Entity entity, Object property)
	{
		super.entityChanged(entity, property);

		if(property == StackableItem.PROP_QUANTITY) {
			quantityChanged = true;
		}
	}
}
