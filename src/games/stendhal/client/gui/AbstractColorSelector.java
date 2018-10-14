/***************************************************************************
 *                   (C) Copyright 2003 - 2015 Faiumoni e.V.               *
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import games.stendhal.client.gui.j2d.Blend;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteCache;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.MathHelper;
import games.stendhal.common.color.ARGB;
import games.stendhal.common.color.HSL;

/**
 * Base class for the outfit color selectors.
 *
 * @param <T> selection model type
 */
public abstract class AbstractColorSelector<T extends ColorSelectionModel> extends JPanel {
	/** Selection model. */
	private final T model;

	/**
	 * Construct a selector from a model.
	 *
	 * @param model selection model
	 */
	AbstractColorSelector(T model) {
		this.model = model;
		setBorder(null);
		setLayout(new SBoxLayout(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING));
	}

	/**
	 * Get the selection model.
	 *
	 * @return selection model
	 */
	T getSelectionModel() {
		return model;
	}

	/**
	 * Base class for the color selector sliders.
	 * @param <T> selection model type
	 */
	abstract static class AbstractSelector<T extends ColorSelectionModel> extends JComponent implements ChangeListener {
		/** Model to adjust and listen to. */
		final T model;

		/**
		 * Create a new Selector.
		 *
		 * @param model selection model
		 */
		AbstractSelector(T model) {
			this.model = model;
			model.addChangeListener(this);
			setOpaque(true);
			applyStyle();
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent ev) {
					if (isEnabled()) {
						select(ev.getPoint());
					}
				}
			});

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent ev) {
					if (isEnabled()) {
						select(ev.getPoint());
					}
				}
			});
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			// Colors changed
			repaint();
		}

		/**
		 * Apply Stendhal style.
		 */
		private void applyStyle() {
			Style style = StyleUtil.getStyle();
			if (style != null) {
				setBorder(style.getBorderDown());
			}
		}

		/**
		 * User clicked a point, or dragged the adjuster to it. The component
		 * should recalculate the colors.
		 *
		 * @param point clicked point
		 */
		abstract void select(Point point);
	}

	/**
	 * Base class for color selectors that are based on a sprite.
	 *
	 * @param <T> selection model type
	 */
	abstract static class AbstractSpriteColorSelector<T extends ColorSelectionModel> extends AbstractSelector<T> {
		/** Width of the generated sprite. */
		static final int SPRITE_WIDTH = 80;
		/** Height of the generated sprite. */
		static final int SPRITE_HEIGHT = 52;

		/** Background sprites. */
		private Sprite normalSprite, disabledSprite;

		/**
		 * Construct a new AbstractSpriteColorSelector.
		 *
		 * @param model model for the selector
		 */
		AbstractSpriteColorSelector(T model) {
			super(model);
		}

		/**
		 * Create the normal, colored background sprite. The dimensions should
		 * be {@link #SPRITE_WIDTH} × {@link #SPRITE_HEIGHT}.
		 *
		 * @return background sprite
		 */
		abstract Sprite createNormalSprite();

		/**
		 * Fetch the normal background sprite.
		 *
		 * @return colored background sprite
		 */
		private Sprite getNormalSprite() {
			if (normalSprite == null) {
				normalSprite = createNormalSprite();
			}
			return normalSprite;
		}

		/**
		 * Get the current background sprite.
		 *
		 * @return current background
		 */
		Sprite getBackgroundSprite() {
			if (isEnabled()) {
				return getNormalSprite();
			} else {
				if (disabledSprite == null) {
					Sprite orig = getNormalSprite();
					if (orig.getReference() != null) {
						// Ensure it's cached so that the automatic lookup for
						// colored version works
						SpriteCache.get().add(orig.getReference(), orig);
						disabledSprite = SpriteStore.get().getColoredSprite(orig.getReference().toString(), Color.GRAY);
					} else {
						disabledSprite = SpriteStore.get().modifySprite(orig, Color.GRAY, Blend.TrueColor, null);
					}
				}
				return disabledSprite;
			}
		}

		@Override
		public Dimension getPreferredSize() {
			Sprite s = getBackgroundSprite();
			int width = s.getWidth();
			int height = s.getHeight();
			Insets ins = getInsets();
			width += ins.left + ins.right;
			height += ins.top + ins.bottom;
			return new Dimension(width, height);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Insets ins = getInsets();
			Sprite sprite = getBackgroundSprite();
			sprite.draw(g, ins.left, ins.right);
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

	/**
	 * Lightness part of the selector.
	 */
	static class LightnessSelector extends AbstractSelector<HSLSelectionModel> {
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
}
