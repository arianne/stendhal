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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

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
	private static class SkinPaletteSelector extends AbstractSelector<SkinColorSelectionModel> {
		private static final String SKIN_PALETTE_IMAGE = "data/gui/colors_skin.png";
		/** background sprite */
		Sprite paletteSprite;
		
		/** Color mapping */
		List<List<SkinColor>> colorMap = Arrays.asList(
				Arrays.asList( // Row 1
						SkinColor.COLOR1,
						SkinColor.COLOR2,
						SkinColor.COLOR3,
						SkinColor.COLOR4),
				Arrays.asList( // Row 2
						SkinColor.COLOR5,
						SkinColor.COLOR6,
						SkinColor.COLOR7,
						SkinColor.COLOR8),
				Arrays.asList( // Row 3
						SkinColor.COLOR9,
						SkinColor.COLOR10,
						SkinColor.COLOR11,
						SkinColor.COLOR12),
				Arrays.asList( // Row 4
						SkinColor.COLOR13,
						SkinColor.COLOR14,
						SkinColor.COLOR15,
						SkinColor.COLOR16)
					);
		
		/**
		 * Create a new SkinPaletteSelector.
		 * @param model selection model
		 */
		SkinPaletteSelector(SkinColorSelectionModel model) {
			super(model);
		}

		/**
		 * Get the color gradient sprite.
		 * 
		 * @return background sprite
		 */
		private Sprite getPaletteSprite() {
			if (paletteSprite == null) {
				if (isEnabled()) {
					paletteSprite = SpriteStore.get().getSprite(SKIN_PALETTE_IMAGE);
				} else {
					// Desaturated image for disabled selector
					paletteSprite = SpriteStore.get().getColoredSprite(SKIN_PALETTE_IMAGE, Color.GRAY);
				}
			}

			return paletteSprite;
		}

		@Override
		public Dimension getPreferredSize() {
			Sprite s = getPaletteSprite();
			int width = s.getWidth();
			int height = s.getHeight();
			Insets ins = getInsets();
			width += ins.left + ins.right;
			height += ins.top + ins.bottom;
			return new Dimension(width, height);
		}

		@Override
		public void paintComponent(Graphics g) {
			Insets ins = getInsets();
			Sprite sprite = getPaletteSprite();
			sprite.draw(g, ins.left, ins.right);
		}

		@Override
		void select(Point point) {
			Sprite sprite = getPaletteSprite();
			
			// Dimensions
			int width = sprite.getWidth();
			int height = sprite.getHeight();
			int column_count = colorMap.size();
			int row_count = colorMap.get(0).size();
			int colorWidth = width / column_count;
			int colorHeight = height / row_count; // 4 rows
			
			int column, row;
			column = ((point.y / colorHeight));
			row = ((point.x / colorWidth));
			
			/* Cursor position is tracked outside of selector area if mouse
			 * button is held down. Must reset row and column to minimun or
			 * maximum values in this case
			 */
			column = Math.max(0, Math.min(column, column_count - 1));
			row = Math.max(0, Math.min(row, row_count - 1));
			
			SkinColor selectedColor = colorMap.get(column).get(row);
			model.setSelectedColor(selectedColor);
		}
		
		@Override
		public void setEnabled(boolean enabled) {
			boolean old = isEnabled();
			super.setEnabled(enabled);
			if (old != enabled) {
				// Force sprite change
				paletteSprite = null;
				repaint();
			}
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
