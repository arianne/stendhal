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
	AbstractColorSelector() {
		setBorder(null);
		setLayout(new SBoxLayout(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING));
	}
	
	/**
	 * Get the selection model.
	 * 
	 * @return selection model
	 */
	abstract T getSelectionModel();
	
	/**
	 * Base class for the color selector sliders.
	 * @param <T> selection model type
	 */
	static abstract class AbstractSelector<T extends ColorSelectionModel> extends JComponent implements ChangeListener {
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
		 * @param point
		 */
		abstract void select(Point point);
	}
}
