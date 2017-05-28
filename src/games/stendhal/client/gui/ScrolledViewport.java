/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 * A JScrollPane variant that hides the child components border completely when
 * the scroll bars are not shown.
 */
public class ScrolledViewport {
	private final JScrollPane scrollPane;
	private final JComponent view;
	private final Border originalBorder;

	/**
	 * Create a new ScrolledViewport.
	 *
	 * @param view child component. The border that view has at the moment when
	 * 	the ScrolledViewport is created is used when ant least one of the scroll
	 * 	bars is visible.
	 */
	public ScrolledViewport(JComponent view) {
		this.view = view;
		originalBorder = view.getBorder();
		scrollPane = new JScrollPane(view);
		ComponentListener listener = new ScrollBarVisibilityChangeListener();
		scrollPane.getHorizontalScrollBar().addComponentListener(listener);
		scrollPane.getVerticalScrollBar().addComponentListener(listener);
	}

	/**
	 * Get the enclosing component.
	 *
	 * @return JScrollPane containing the view
	 */
	public JComponent getComponent() {
		return scrollPane;
	}

	/**
	 * Set the scrolling speed when using the mouse wheel.
	 *
	 * @param speed
	 */
	public void setScrollingSpeed(int speed) {
		scrollPane.getHorizontalScrollBar().setUnitIncrement(speed);
		scrollPane.getVerticalScrollBar().setUnitIncrement(speed);
	}

	/**
	 * Listener for following scroll bar visibility changes.
	 */
	private class ScrollBarVisibilityChangeListener extends ComponentAdapter {
		@Override
		public void componentHidden(ComponentEvent e) {
			updateBorder();
		}

		@Override
		public void componentShown(ComponentEvent e) {
			updateBorder();
		}

		/**
		 * Show the border if at least one of the scroll bars is visible, hide
		 * it otherwise.
		 */
		private void updateBorder() {
			if (scrollPane.getHorizontalScrollBar().isVisible()
				|| scrollPane.getVerticalScrollBar().isVisible()) {
				view.setBorder(originalBorder);
			} else {
				view.setBorder(null);
			}
		}
	}
}
