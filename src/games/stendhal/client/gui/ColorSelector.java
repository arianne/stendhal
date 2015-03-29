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

import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.color.ARGB;
import games.stendhal.common.color.HSL;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.colorchooser.DefaultColorSelectionModel;

/**
 * A HSL space color selector that should be small enough to fit in the outfit
 * selection dialog.
 */
class ColorSelector extends AbstractColorSelector<ColorSelector.HSLSelectionModel> {
	private final HSLSelectionModel model;
	private final JComponent hueSaturationSelector;
	private final JComponent lightnessSelector;


	/**
	 * Create a new ColorSelector.
	 */
	ColorSelector() {
		model = new HSLSelectionModel();
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
	 * Get the selection model.
	 * 
	 * @return selection model
	 */
	@Override
	HSLSelectionModel getSelectionModel() {
		return model;
	}
	
	/**
	 * Hue-Saturation part of the selector component.
	 */
	private static class HueSaturationSelector extends AbstractSpriteColorSelector<HSLSelectionModel> {
		private static final String HUE_SATURATION_IMAGE = "data/gui/colors.png";

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
		Sprite createSprite() {
			if (isEnabled()) {
				return SpriteStore.get().getSprite(HUE_SATURATION_IMAGE);
			} else {
				// Desaturated image for disabled selector
				return SpriteStore.get().getColoredSprite(HUE_SATURATION_IMAGE, Color.GRAY);
			}
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
			xDiff = Math.min(width, Math.max(0, xDiff));
			float hue = xDiff / (float) width;
			int yDiff = point.y - ins.top;
			yDiff = Math.min(height, Math.max(0, yDiff));
			float saturation = 1f - yDiff / (float) height;
			model.setHS(hue, saturation);
		}
	}

	/**
	 * Lightness part of the selector.
	 */
	private static class LightnessSelector extends AbstractSelector<HSLSelectionModel> {
		/** Height of the gradient bar. */
		private static final int BAR_HEIGHT = 10;

		/**
		 * Create a new LightnessSelector.
		 * 
		 * @param model selection model. Should be the same as for the entire
		 * 	selector component.
		 */
		LightnessSelector(HSLSelectionModel model) {
			super(model);
		}

		@Override
		public Dimension getPreferredSize() {
			Insets ins = getInsets();
			// insane value, but we do not actually use it
			int width = BAR_HEIGHT + ins.left + ins.right;
			int height = BAR_HEIGHT + ins.top + ins.bottom;
			return new Dimension(width, height);
		}

		@Override
		public void paintComponent(Graphics g) {
			Insets ins = getInsets();
			int width = getWidth() - ins.left - ins.right;
			int height = getWidth() - ins.left - ins.right;

			if (isEnabled()) {
				/*
				 * A gradient paint is a bit fake, as it won't use the same
				 * color model for the shift as we actually do. However, that
				 * should not be a problem because we never show the user the
				 * actual selected color, so she won't be able to see the slight
				 * difference.
				 * 
				 * Do the paint in 2 parts, to get the correct saturation for
				 * the mid lightness.
				 */
				// calculate start, end and middle colors
				float[] hsl = new float[3];
				int[] rgb = new int[4];
				rgb[0] = 0xff; // alpha

				hsl[0] = model.getHue();
				hsl[1] = model.getSaturation();
				// 0 would be black, and have no color
				hsl[2] = 0.08f;
				HSL.hsl2rgb(hsl, rgb);
				Color startColor = new Color(ARGB.mergeRgb(rgb));
				hsl[2] = 0.5f;
				HSL.hsl2rgb(hsl, rgb);
				Color midColor = new Color(ARGB.mergeRgb(rgb));
				// 1 would be white, and have no color
				hsl[2] = 0.92f;
				HSL.hsl2rgb(hsl, rgb);
				Color endColor = new Color(ARGB.mergeRgb(rgb));
			
				Graphics2D g2d = (Graphics2D) g;
				GradientPaint p = new GradientPaint(ins.left, ins.top, startColor, width / 2f, ins.top, midColor);
				g2d.setPaint(p);
				g2d.fillRect(ins.left, ins.top, width / 2, height);

				p = new GradientPaint(ins.left + width / 2f, ins.top, midColor, width, ins.top, endColor);
				g2d.setPaint(p);
				g2d.fillRect(ins.left + width / 2, ins.top, width / 2, height);
			} else {
				// Fake a desaturated gradient.
				Color startColor = Color.BLACK;
				Color endColor = Color.WHITE;
		
				Graphics2D g2d = (Graphics2D) g;
				GradientPaint p = new GradientPaint(ins.left, ins.top, startColor, width, ins.top, endColor);
				g2d.setPaint(p);
				g2d.fillRect(ins.left, ins.top, width, height);
			}
			
			// Draw a line. white is not visible on black, and the vice versa,
			// so draw them both
			g.setColor(Color.BLACK);
			int x = (int) (model.getLightness() * width) + ins.left;
			g.drawLine(x, 0, x, getHeight());
			g.setColor(Color.WHITE);
			x++;
			g.drawLine(x, 0, x, getHeight());
		}

		@Override
		void select(Point point) {
			Insets ins = getInsets();
			int width = getWidth() - ins.left - ins.right;
			int xDiff = point.x - ins.left;
			xDiff = Math.min(width, Math.max(0, xDiff));
			float lightness = xDiff / (float) width;
			/*
			 * Limit lightness a bit, so that the gradient does not become
			 * confusingly desaturated at the ends.
			 */
			lightness = Math.max(0.01f, Math.min(0.99f, lightness));
			model.setL(lightness);
		}
		
		@Override
		public void setEnabled(boolean enabled) {
			boolean old = isEnabled();
			super.setEnabled(enabled);
			if (old != enabled) {
				repaint();
			}
		}
	}

	/**
	 * Color selection model that is capable of returning, and accepting HSL
	 * space color data in addition of the usual RGB.
	 */
	static class HSLSelectionModel extends DefaultColorSelectionModel {
		/** Current color in HSL space. */
		private float[] hsl = new float[3];

		@Override
		public void setSelectedColor(Color color) {
			if (color != null) {
				super.setSelectedColor(color);
			} else {
				// Something with a sane lightness value
				super.setSelectedColor(Color.GRAY);
			}
			int[] rgb = new int[4];
			ARGB.splitRgb(getSelectedColor().getRGB(), rgb);
			HSL.rgb2hsl(rgb, hsl);
			fireStateChanged();
		}

		/**
		 * Set hue and saturation.
		 * 
		 * @param hue
		 * @param saturation
		 */
		void setHS(float hue, float saturation) {
			hsl[0] = hue;
			hsl[1] = saturation;
			updateColor();
		}

		/**
		 * Set lightness
		 * 
		 * @param lightness
		 */
		void setL(float lightness) {
			hsl[2] = lightness;
			updateColor();
		}

		/**
		 * Get hue.
		 * 
		 * @return hue
		 */
		float getHue() {
			return hsl[0];
		}

		/**
		 * Get saturation.
		 * 
		 * @return saturation
		 */
		float getSaturation() {
			return hsl[1];
		}

		/**
		 * Get lightness.
		 * 
		 * @return lightness
		 */
		float getLightness() {
			return hsl[2];
		}

		/**
		 * Recalculate color based on the HSL data.
		 */
		private void updateColor() {
			int[] rgb = new int[4];
			HSL.hsl2rgb(hsl, rgb);
			rgb[0] = 0xff;
			super.setSelectedColor(new Color(ARGB.mergeRgb(rgb)));
			fireStateChanged();
		}
	}
}
