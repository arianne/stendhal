/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;


import java.awt.Color;
import java.awt.Graphics2D;

import games.stendhal.client.entity.StackableItem;
import games.stendhal.client.gui.j2d.entity.helpers.DrawingHelper;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.TextSprite;

/**
 * The 2D view of a stackable item.
 *
 * @param <T> stackable item type
 */
public class StackableItem2DView<T extends StackableItem> extends Item2DView<T> {

	/**
	 * The quantity value changed.
	 */
	private volatile boolean quantityChanged;

	/**
	 * The image of the quantity.
	 */
	private Sprite quantitySprite;

	/**
	 * Whether to show the quantity.
	 */
	private boolean showQuantity;

	@Override
	public void initialize(final T entity) {
		super.initialize(entity);
		quantitySprite = getQuantitySprite(entity);
		quantityChanged = false;
		showQuantity = true;
	}

	//
	// StackableItem2DView
	//

	/**
	 * Get the appropriate quantity sprite.
	 *
	 * @param entity
	 * @return A sprite representing the quantity, or <code>null</code> for
	 *         none.
	 */
	private Sprite getQuantitySprite(T entity) {
		int quantity;
		String label;

		quantity = entity.getQuantity();

		if (quantity <= 1) {
			return null;
		} else if (isContained() && (quantity > 9999999)) {
			label = (quantity / 1000000) + "M";
		} else if (isContained() && (quantity > 9999)) {
			label = (quantity / 1000) + "K";
		} else {
			label = Integer.toString(quantity);
		}

		return TextSprite.createTextSprite(label, Color.WHITE);
	}

	/**
	 * Set whether to show the quantity value.
	 *
	 * @param showQuantity
	 *            Whether to show the quantity.
	 */
	public void setShowQuantity(final boolean showQuantity) {
		this.showQuantity = showQuantity;
	}

	//
	// Entity2DView
	//

	@Override
	protected void draw(final Graphics2D g2d, final int x, final int y,
			final int width, final int height) {
		super.draw(g2d, x, y, width, height);

		if (showQuantity && (quantitySprite != null)) {
			drawQuantity(g2d, x, y, width, height);
		}
	}

	/**
	 * Draw quantity sprite. Exact position depends on containment status.
	 *
	 * @param g2d graphics
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param width width of the drawing area
	 * @param height height of the drawing area
	 */
	private void drawQuantity(final Graphics2D g2d, final int x, final int y,
			final int width, int height) {
		if (isContained()) {
			// Right alignment
			DrawingHelper.drawAlignedSprite(g2d, quantitySprite, HorizontalAlignment.RIGHT, VerticalAlignment.TOP, x, y, width, height);
		} else {
			// Center alignment
			DrawingHelper.drawAlignedSprite(g2d, quantitySprite, HorizontalAlignment.CENTER, VerticalAlignment.TOP, x, y, width, height);
		}
	}

	/**
	 * Set whether this view is contained, and should render in a compressed
	 * (it's defined) area without clipping anything important.
	 *
	 * @param contained
	 *            <code>true</code> if contained.
	 */
	@Override
	public void setContained(final boolean contained) {
		super.setContained(contained);

		quantityChanged = true;
		markChanged();
	}

	/**
	 * Update representation.
	 */
	@Override
	protected void update() {
		super.update();

		T entity  = this.entity;
		if (quantityChanged && (entity != null)) {
			quantityChanged = false;
			quantitySprite = getQuantitySprite(entity);
		}
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == StackableItem.PROP_QUANTITY) {
			quantityChanged = true;
		}
	}
}
