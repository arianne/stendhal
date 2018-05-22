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
import javax.swing.colorchooser.DefaultColorSelectionModel;

import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.common.MathHelper;
import games.stendhal.common.color.ARGB;
import games.stendhal.common.color.HSL;
import games.stendhal.common.constants.SkinColor;

/**
 * A HSL space color selector that should be small enough to fit in the outfit
 * selection dialog.
 */
class SkinColorSelector extends AbstractColorSelector<SkinColorSelector.SkinColorSelectionModel> {
	/** Palette selector component. */
	private final JComponent paletteSelector;

	/**
	 * Create a new ColorSelector.
	 */
	SkinColorSelector() {
		super(new SkinColorSelectionModel());
		paletteSelector = new SkinPaletteSelector(getSelectionModel());
		add(paletteSelector);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		paletteSelector.setEnabled(enabled);
	}

	/**
	 * Skin color part of the selector component.
	 */
	private static class SkinPaletteSelector extends AbstractSpriteColorSelector<SkinColorSelectionModel> {
		/** Width and height of the color patches. */
		private static final int COLOR_ITEM_WIDTH, COLOR_ITEM_HEIGHT;
		/** Color mapping. */
		private static final SkinColor[][] COLOR_MAP;

		/** Currently selected row and column. */
		private int row, column;

		static {
			/* Construct the color map. */
			SkinColor[] allValues = SkinColor.values();
			int numColors = allValues.length;
			int width = 1, height = 1;
			int bound = (int) Math.ceil(Math.sqrt(numColors));
			for (int i = bound; i >= 1; i--) {
				if (numColors % i == 0) {
					width = i;
					height = numColors / i;
					break;
				}
			}
			COLOR_MAP = new SkinColor[height][];
			int index = 0;
			for (int y = 0; y < height; y++) {
				SkinColor[] arr = new SkinColor[width];
				COLOR_MAP[y] = arr;
				for (int x = 0; x < width; x++) {
					arr[x] = allValues[index];
					index++;
				}
			}

			COLOR_ITEM_WIDTH = SPRITE_WIDTH / width;
			COLOR_ITEM_HEIGHT = SPRITE_HEIGHT / height;
		}

		/**
		 * Create a new SkinPaletteSelector.
		 * @param model selection model
		 */
		SkinPaletteSelector(SkinColorSelectionModel model) {
			super(model);
		}

		/**
		 * Create the color patch sprite.
		 *
		 * @return created sprite
		 */
		@Override
		Sprite createNormalSprite() {
			BufferedImage img = getGraphicsConfiguration().createCompatibleImage(SPRITE_WIDTH, SPRITE_HEIGHT);
			Graphics g = img.getGraphics();

			for (int y = 0; y < COLOR_MAP.length; y++) {
				for (int x = 0; x < COLOR_MAP[0].length; x++) {
					/**
					 * Fake the color a bit. The TrueColor blend just bends
					 * a bit the lightness of the image to the direction of the
					 * color so the color may look too light or too dark to the
					 * user compared to the result. Adjust the lightness of the
					 * displayed color to compensate.
					 */
					int color = COLOR_MAP[y][x].getColor();
					int[] argb = new int[4];
					ARGB.splitRgb(color, argb);
					float[] hsl = new float[3];
					HSL.rgb2hsl(argb, hsl);
					float lightness = hsl[2];
					lightness = (lightness - 0.5f) * 0.75f + 0.45f;
					hsl[2] = lightness;
					HSL.hsl2rgb(hsl, argb);
					color = ARGB.mergeRgb(argb);
					
					g.setColor(new Color(color));
					g.fillRect(x * COLOR_ITEM_WIDTH, y * COLOR_ITEM_HEIGHT, COLOR_ITEM_WIDTH, COLOR_ITEM_HEIGHT);
				}
			}
			g.dispose();
			return new ImageSprite(img, "skin_color_selection_id");
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			// Highlight the selection
			g.setColor(Color.WHITE);
			Insets ins = getInsets();
			int x = ins.left + column * COLOR_ITEM_WIDTH;
			int y = ins.top + row * COLOR_ITEM_HEIGHT;
			g.drawRect(x, y, COLOR_ITEM_WIDTH - 1, COLOR_ITEM_HEIGHT - 1);
		}

		@Override
		void select(Point point) {
			Insets ins = getInsets();
			row = (point.y - ins.top) / COLOR_ITEM_HEIGHT;
			column = (point.x - ins.left) / COLOR_ITEM_WIDTH;

			/* Cursor position is tracked outside of selector area if mouse
			 * button is held down. Must reset row and column to minimun or
			 * maximum values in this case
			 */
			column = MathHelper.clamp(column, 0, COLOR_MAP.length - 1);
			row = MathHelper.clamp(row, 0, COLOR_MAP[0].length - 1);

			SkinColor selectedColor = COLOR_MAP[row][column];
			model.setSelectedColor(selectedColor);
		}
	}

	/**
	 * Color selection model that is capable of returning, and accepting HSL
	 * space color data in addition of the usual RGB.
	 */
	static class SkinColorSelectionModel extends DefaultColorSelectionModel {
		/** The enum corresponding to current color. */
		private SkinColor enumColor;

		/**
		 * Set the selected skin color.
		 *
		 * @param color new color
		 */
		void setSelectedColor(SkinColor color) {
			if (color == enumColor) {
				return;
			}
			enumColor = color;
			super.setSelectedColor(new Color(enumColor.getColor()));
		}

		/**
		 * Used for setting outfit colors other than skin.
		 *
		 * @param color
		 * 		Target outfit color
		 */
		@Override
		public void setSelectedColor(Color color) {
			if (color != null) {
				enumColor = SkinColor.fromInteger(color.getRGB());
			} else {
				enumColor = SkinColor.COLOR1;
			}
			super.setSelectedColor(new Color(enumColor.getColor()));
		}
	}
}
