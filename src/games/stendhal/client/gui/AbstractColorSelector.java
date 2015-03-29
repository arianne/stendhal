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

import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.sprite.Sprite;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
		/** Background sprite. */
		private Sprite background;
		
		/**
		 * Construct a new AbstractSpriteColorSelector.
		 * 
		 * @param model model for the selector
		 */
		AbstractSpriteColorSelector(T model) {
			super(model);
		}
		
		/**
		 * Create or fetch the appropriate background sprite for current state.
		 *
		 * @return appropriate sprite for the current enabled/disabled state of
		 *	the selector
		 */
		abstract Sprite createSprite();
		
		/**
		 * Get the current background sprite.
		 * 
		 * @return current background
		 */
		Sprite getBackgroundSprite() {
			if (background == null) {
				background = createSprite();
			}
			return background;
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
				// Force sprite change
				background = null;
				repaint();
			}
		}
	}
}
