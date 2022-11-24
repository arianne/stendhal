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
import games.stendhal.common.constants.SkinColor;

/**
 * A HSL space color selector that should be small enough to fit in the outfit
 * selection dialog.
 */
class SkinColorSelector extends AbstractColorSelector<SkinColorSelector.SkinColorSelectionModel> {
	/** Palette selector component. */
	private final JComponent paletteSelector;
	private final JComponent lightnessSelector;

	/**
	 * Create a new ColorSelector.
	 */
	SkinColorSelector() {
		super(new SkinColorSelectionModel());
		paletteSelector = new SkinPaletteSelector(getSelectionModel());
		add(paletteSelector);
		lightnessSelector = new LightnessSelector(getSelectionModel());
		add(lightnessSelector, SLayout.EXPAND_X);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		paletteSelector.setEnabled(enabled);
		lightnessSelector.setEnabled(enabled);
	}

	/**
	 * Skin color part of the selector component.
	 */
	private static class SkinPaletteSelector extends AbstractSpriteColorSelector<SkinColorSelectionModel> {
		/** Width and height of the color patches. */
		private static final int COLOR_ITEM_WIDTH, COLOR_ITEM_HEIGHT;
		/** Color mapping. */
		private static final SkinColor[][] COLOR_MAP;

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
					g.setColor(new Color(COLOR_MAP[y][x].getColor()));
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
			int idx = model.getSkinColor().ordinal();
			int row = idx / COLOR_MAP[0].length;
			int column = idx % COLOR_MAP.length;
			int x = ins.left + column * COLOR_ITEM_WIDTH;
			int y = ins.top + row * COLOR_ITEM_HEIGHT;
			g.drawRect(x, y, COLOR_ITEM_WIDTH - 1, COLOR_ITEM_HEIGHT - 1);
		}

		@Override
		void select(Point point) {
			Insets ins = getInsets();
			int row = (point.y - ins.top) / COLOR_ITEM_HEIGHT;
			int column = (point.x - ins.left) / COLOR_ITEM_WIDTH;

			/* Cursor position is tracked outside of selector area if mouse
			 * button is held down. Must reset row and column to minimun or
			 * maximum values in this case
			 */
			column = MathHelper.clamp(column, 0, COLOR_MAP.length - 1);
			row = MathHelper.clamp(row, 0, COLOR_MAP[0].length - 1);

			SkinColor selectedColor = COLOR_MAP[row][column];
			model.setSelectedColor(selectedColor);
			/*
			 * At high and low lightness changing the base color does not
			 * always change the resulting color. Force a repaint so that
			 * the new base color get highlighted.
			 */
			repaint();
		}
	}

	/**
	 * Color selection model that is capable of returning, and accepting HSL
	 * space color data in addition of the usual RGB.
	 */
	static class SkinColorSelectionModel extends HSLSelectionModel {
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
			setSelectedColor(new Color(enumColor.getColor()));
		}

		SkinColor getSkinColor() {
			return enumColor;
		}

		@Override
		public void setSelectedColor(Color color) {
			if (color != null) {
				boolean keepLightness = enumColor != null;
				enumColor = SkinColor.fromInteger(color.getRGB());

				int[] argb = new int[4];
				ARGB.splitRgb(enumColor.getColor(), argb);
				float[] hsl = new float[3];
				HSL.rgb2hsl(argb, hsl);
				setHS(hsl[0], hsl[1]);
				if (!keepLightness) {
					/*
					 * Take lightness from the set color - it is coming
					 * from the current player outfit
					 */
					ARGB.splitRgb(color.getRGB(), argb);
					HSL.rgb2hsl(argb, hsl);
					setL(hsl[2]);
				}
			} else {
				enumColor = SkinColor.COLOR1;
				super.setSelectedColor(new Color(enumColor.getColor()));
			}
		}
	}
}
