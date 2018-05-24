/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import games.stendhal.client.gui.AbstractColorSelector.HSLSelectionModel;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.common.MathHelper;
import games.stendhal.common.color.ARGB;
import games.stendhal.common.color.HSL;

/**
 * A HSL space color selector that should be small enough to fit in the outfit
 * selection dialog.
 */
class ColorSelector extends AbstractColorSelector<HSLSelectionModel> {
	/** The H-S selection area. */
	private final JComponent hueSaturationSelector;
	/** Lightness slider. */
	private final JComponent lightnessSelector;

	/**
	 * Create a new ColorSelector.
	 */
	ColorSelector() {
		super(new HSLSelectionModel());
		HSLSelectionModel model = getSelectionModel();
		hueSaturationSelector = new HueSaturationSelector(model);
		add(hueSaturationSelector);
		lightnessSelector = new LightnessSelector(model);
		add(lightnessSelector, SLayout.EXPAND_X);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		hueSaturationSelector.setEnabled(enabled);
		lightnessSelector.setEnabled(enabled);
	}

	/**
	 * Hue-Saturation part of the selector component.
	 */
	private static class HueSaturationSelector extends AbstractSpriteColorSelector<HSLSelectionModel> {
		/**
		 * Create a new HueSaturationSelector.
		 *
		 * @param model selection model. Should be the same as for the whole
		 * 	selector
		 */
		HueSaturationSelector(HSLSelectionModel model) {
			super(model);
		}

		@Override
		Sprite createNormalSprite() {
			BufferedImage img = getGraphicsConfiguration().createCompatibleImage(SPRITE_WIDTH, SPRITE_HEIGHT);

			float[] hsl = new float[3];
			hsl[2] = 0.5f;
			int[] rgb = new int[4];
			rgb[0] = 0xff;

			for (int x = 0; x < SPRITE_WIDTH; x++) {
				for (int y = 0; y < SPRITE_HEIGHT; y++) {
					hsl[0] = x / (float) SPRITE_WIDTH;
					hsl[1] = 1f - y / (float) SPRITE_HEIGHT;
					HSL.hsl2rgb(hsl, rgb);
					int color = ARGB.mergeRgb(rgb);
					img.setRGB(x, y, color);
				}
			}
			return new ImageSprite(img, "hsl_color_selection_id");
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Insets ins = getInsets();
			Sprite sprite = getBackgroundSprite();

			// draw a cross
			g.setColor(Color.BLACK);
			int x = (int) (model.getHue() * sprite.getWidth()) + ins.left;
			int y = (int) ((1f - model.getSaturation()) * sprite.getHeight()) + ins.left;
			g.drawLine(x, 0, x, getHeight());
			g.drawLine(0, y, getWidth(), y);
		}

		@Override
		void select(Point point) {
			Insets ins = getInsets();
			Sprite sprite = getBackgroundSprite();
			int width = sprite.getWidth();
			int height = sprite.getHeight();
			int xDiff = point.x - ins.left;
			xDiff = MathHelper.clamp(xDiff, 0, width);
			float hue = xDiff / (float) width;
			int yDiff = point.y - ins.top;
			yDiff = MathHelper.clamp(yDiff, 0, height);
			float saturation = 1f - yDiff / (float) height;
			model.setHS(hue, saturation);
		}
	}
}
