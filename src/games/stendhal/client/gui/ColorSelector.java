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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.colorchooser.DefaultColorSelectionModel;

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
class ColorSelector extends AbstractColorSelector<ColorSelector.HSLSelectionModel> {
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
		protected void paintComponent(Graphics g) {
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
				Color[] colors = new Color[3];
				// 0 would be black, and have no color
				hsl[2] = 0.08f;
				HSL.hsl2rgb(hsl, rgb);
				colors[0] = new Color(ARGB.mergeRgb(rgb));
				hsl[2] = 0.5f;
				HSL.hsl2rgb(hsl, rgb);
				colors[1] = new Color(ARGB.mergeRgb(rgb));
				// 1 would be white, and have no color
				hsl[2] = 0.92f;
				HSL.hsl2rgb(hsl, rgb);
				colors[2] = new Color(ARGB.mergeRgb(rgb));
				Paint paint = new LinearGradientPaint(ins.left, 0f, width, 0f,
						new float[]{0f,  0.5f, 1f}, colors);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setPaint(paint);
				g2d.fillRect(ins.left, ins.top, width, height);
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
			xDiff = MathHelper.clamp(xDiff, 0, width);
			float lightness = xDiff / (float) width;
			/*
			 * Limit lightness a bit, so that the gradient does not become
			 * confusingly desaturated at the ends.
			 */
			lightness = MathHelper.clamp(lightness, 0.01f, 0.99f);
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
			if (color == null) {
				// Something with a sane lightness value
				color = Color.GRAY;
			}
			int[] rgb = new int[4];
			ARGB.splitRgb(color.getRGB(), rgb);
			HSL.rgb2hsl(rgb, hsl);
			super.setSelectedColor(color);
		}

		/**
		 * Set hue and saturation.
		 *
		 * @param hue new hue value
		 * @param saturation new saturation value
		 */
		void setHS(float hue, float saturation) {
			hsl[0] = hue;
			hsl[1] = saturation;
			updateColor();
		}

		/**
		 * Set lightness.
		 *
		 * @param lightness new L value
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
		}
	}
}
