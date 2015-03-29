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

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.constants.SkinColor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.colorchooser.DefaultColorSelectionModel;

/**
 * A HSL space color selector that should be small enough to fit in the outfit
 * selection dialog.
 */
class SkinColorSelector extends AbstractColorSelector<SkinColorSelector.SkinColorSelectionModel> {
	private final SkinColorSelectionModel model;
	private final JComponent paletteSelector;
	
	/**
	 * Create a new ColorSelector.
	 */
	SkinColorSelector() {
		model = new SkinColorSelectionModel();
		paletteSelector = new SkinPaletteSelector(model);
		add(paletteSelector);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		paletteSelector.setEnabled(enabled);
	}

	/**
	 * Get the selection model.
	 * 
	 * @return selection model
	 */
	@Override
	SkinColorSelectionModel getSelectionModel() {
		return model;
	}
	
	
	/**
	 * Skin color part of the selector component.
	 */
	private static class SkinPaletteSelector extends AbstractSpriteColorSelector<SkinColorSelectionModel> {
		private static final String SKIN_PALETTE_IMAGE = "data/gui/colors_skin.png";
		/** Currently selected row and column. */
		int row, column;
		/** Width and height of the color patches. */
		int colorItemWidth, colorItemHeight;
		
		/** Color mapping */
		final SkinColor[][] colorMap;
		
		/**
		 * Create a new SkinPaletteSelector.
		 * @param model selection model
		 */
		SkinPaletteSelector(SkinColorSelectionModel model) {
			super(model);
			
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
			colorMap = new SkinColor[height][];
			int index = 0;
			for (int y = 0; y < height; y++) {
				SkinColor arr[] = new SkinColor[width];
				colorMap[y] = arr;
				for (int x = 0; x < width; x++) {
					arr[x] = allValues[index];
					index++;
				}
			}
		}

		@Override
		Sprite createSprite() {
			if (isEnabled()) {
				return SpriteStore.get().getSprite(SKIN_PALETTE_IMAGE);
			} else {
				// Desaturated image for disabled selector
				return SpriteStore.get().getColoredSprite(SKIN_PALETTE_IMAGE, Color.GRAY);
			}
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (isEnabled()) {
				// Highlight the selection
				g.setColor(Color.WHITE);
				Insets ins = getInsets();
				int x = ins.left + column * getColorWidth();
				int y = ins.top + row * getColorHeight();
				g.drawRect(x, y, getColorWidth() - 1, getColorHeight() - 1);
			}
		}

		@Override
		void select(Point point) {
			Insets ins = getInsets();
			row = (point.y - ins.top) / getColorHeight();
			column = (point.x - ins.left) / getColorWidth();
			
			/* Cursor position is tracked outside of selector area if mouse
			 * button is held down. Must reset row and column to minimun or
			 * maximum values in this case
			 */
			column = Math.max(0, Math.min(column, colorMap.length - 1));
			row = Math.max(0, Math.min(row, colorMap[0].length - 1));
			
			SkinColor selectedColor = colorMap[column][row];
			model.setSelectedColor(selectedColor);
		}
		
		/**
		 * Get the width of the color patches.
		 * 
		 * @return color patch width
		 */
		private int getColorWidth() {
			if (colorItemWidth == 0) {
				Sprite sprite = getBackgroundSprite();
				int width = sprite.getWidth();
				colorItemWidth = width / colorMap.length;
			}
			return colorItemWidth;
		}
		
		/**
		 * Get the height of the color patches.
		 * 
		 * @return color patch height
		 */
		private int getColorHeight() {
			if (colorItemHeight == 0) {
				Sprite sprite = getBackgroundSprite();
				int height = sprite.getHeight();
				colorItemHeight = height / colorMap[0].length;
			}
			return colorItemHeight;
		}
	}

	/**
	 * Color selection model that is capable of returning, and accepting HSL
	 * space color data in addition of the usual RGB.
	 */
	static class SkinColorSelectionModel extends DefaultColorSelectionModel {
		/** The enum corresponding to current color. */
		private SkinColor enumColor;
		
		void setSelectedColor(SkinColor color) {
			if (color == enumColor) {
				return;
			}
			enumColor = color;
			super.setSelectedColor(new Color(enumColor.getColor()));
			fireStateChanged();
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
			
			fireStateChanged();
		}
	}
}
