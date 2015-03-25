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

import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A HSL space color selector that should be small enough to fit in the outfit
 * selection dialog.
 */
class ColorSelector extends JPanel {
	private final HSLSelectionModel model;
	private final JComponent paletteSelector;
	private final JComponent lightnessSelector;
	
	
	/**
	 * Create a new ColorSelector.
	 */
	ColorSelector() {
		this(false);
	}
	
	/**
	 * Create a new ColorSelector.
	 * 
	 * @param skinColors
	 * 		Skin colors available only
	 */
	ColorSelector(Boolean skinPalette) {
		model = new HSLSelectionModel();
		setBorder(null);
		setLayout(new SBoxLayout(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING));
		if (skinPalette) {
			paletteSelector = new SkinPaletteSelector(model);
			add(paletteSelector);
			lightnessSelector = null;
		} else {
			paletteSelector = new HueSaturationSelector(model);
			add(paletteSelector);
			lightnessSelector = new LightnessSelector(model);
			add(lightnessSelector, SLayout.EXPAND_X);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		paletteSelector.setEnabled(enabled);
		if (lightnessSelector != null) {
			lightnessSelector.setEnabled(enabled);
		}
	}

	/**
	 * Get the selection model.
	 * 
	 * @return selection model
	 */
	ColorSelectionModel getSelectionModel() {
		return model;
	}

	/**
	 * Base class for the color selector sliders.
	 */
	private static abstract class Selector extends JComponent implements ChangeListener {
		/** Model to adjust and listen to. */
		final HSLSelectionModel model;

		/**
		 * Create a new Selector.
		 * 
		 * @param model selection model
		 */
		Selector(HSLSelectionModel model) {
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
		 * @param point
		 */
		abstract void select(Point point);
	}

	/**
	 * Hue-Saturation part of the selector component.
	 */
	private static class HueSaturationSelector extends Selector {
		private static final String HUE_SATURATION_IMAGE = "data/gui/colors.png";
		/** background sprite */
		Sprite hueSprite;

		/**
		 * Create a new HueSaturationSelector.
		 * 
		 * @param model selection model. Should be the same as for the whole
		 * 	selector
		 */
		HueSaturationSelector(HSLSelectionModel model) {
			super(model);
		}

		/**
		 * Get the color gradient sprite.
		 * 
		 * @return background sprite
		 */
		private Sprite getHueSprite() {
			if (hueSprite == null) {
				if (isEnabled()) {
					hueSprite = SpriteStore.get().getSprite(HUE_SATURATION_IMAGE);
				} else {
					// Desaturated image for disabled selector
					hueSprite = SpriteStore.get().getColoredSprite(HUE_SATURATION_IMAGE, Color.GRAY);
				}
			}

			return hueSprite;
		}

		@Override
		public Dimension getPreferredSize() {
			Sprite s = getHueSprite();
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
			Sprite sprite = getHueSprite();
			sprite.draw(g, ins.left, ins.right);

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
			Sprite sprite = getHueSprite();
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
		
		@Override
		public void setEnabled(boolean enabled) {
			boolean old = isEnabled();
			super.setEnabled(enabled);
			if (old != enabled) {
				// Force sprite change
				hueSprite = null;
				repaint();
			}
		}
	}

	/**
	 * Skin color part of the selector component.
	 */
	private static class SkinPaletteSelector extends Selector {
		private static final String SKIN_PALETTE_IMAGE = "data/gui/colors_skin.png";
		/** background sprite */
		Sprite paletteSprite;
		
		// Skin colors to choose from
		private final Color COLOR1 = new Color(0x895426);
		private final Color COLOR2 = new Color(0xbfb25e);
		private final Color COLOR3 = new Color(0xd8d79a);
		private final Color COLOR4 = new Color(0x60502d);
		private final Color COLOR5 = new Color(0xe6dcc5);
		private final Color COLOR6 = new Color(0xba6c45);
		private final Color COLOR7 = new Color(0x917944);
		private final Color COLOR8 = new Color(0x989898);
		
		/**
		 * Create a new SkinPaletteSelector.
		 */
		SkinPaletteSelector(HSLSelectionModel model) {
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
			final Color selectedColor;
			Insets ins = getInsets();
			Sprite sprite = getPaletteSprite();
			int width = sprite.getWidth();
			int height = sprite.getHeight();
			int colorWidth = width / 4; // 4 columns
			int colorHeight = height / 2; // 2 rows
			
			// FIXME: Should be a more optimized way to do this
			if (point.y <= colorHeight) {
				// Top row
				if (point.x <= colorWidth) {
					selectedColor = COLOR1;
				} else if (point.x <= (colorWidth * 2)) {
					selectedColor = COLOR2;
				} else if (point.x <= (colorWidth * 3)) {
					selectedColor = COLOR3;
				} else {
					selectedColor = COLOR4;
				}
			} else {
				// Bottom row
				if (point.x <= colorWidth) {
					selectedColor = COLOR5;
				} else if (point.x <= (colorWidth * 2)) {
					selectedColor = COLOR6;
				} else if (point.x <= (colorWidth * 3)) {
					selectedColor = COLOR7;
				} else {
					selectedColor = COLOR8;
				}
			}
			int xDiff = point.x - ins.left;
			xDiff = Math.min(width, Math.max(0, xDiff));
			float hue = xDiff / (float) width;
			int yDiff = point.y - ins.top;
			yDiff = Math.min(height, Math.max(0, yDiff));
			float saturation = 1f - yDiff / (float) height;
			//model.setHS(hue, saturation);
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
	 * Lightness part of the selector.
	 */
	private static class LightnessSelector extends Selector {
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
	private static class HSLSelectionModel implements ColorSelectionModel {
		/** Listeners following this model. */
		private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
		/** Current color. */
		private Color color;
		/** Current color in HSL space. */
		private float[] hsl = new float[3];

		@Override
		public void addChangeListener(ChangeListener listener) {
			listeners.add(listener);
		}

		@Override
		public Color getSelectedColor() {
			return color;
		}

		@Override
		public void removeChangeListener(ChangeListener listener) {
			listeners.remove(listener);
		}

		@Override
		public void setSelectedColor(Color color) {
			if (color != null) {
				this.color = color;
			} else {
				// Something with a sane lightness value
				this.color = Color.GRAY;
			}
			int[] rgb = new int[4];
			ARGB.splitRgb(this.color.getRGB(), rgb);
			HSL.rgb2hsl(rgb, hsl);
			fireChanged();
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
			color = new Color(ARGB.mergeRgb(rgb));
			fireChanged();
		}

		/**
		 * Notify listeners about changed color.
		 */
		private void fireChanged() {
			for (ChangeListener listener : listeners) {
				listener.stateChanged(new ChangeEvent(this));
			}
		}
	}
}
