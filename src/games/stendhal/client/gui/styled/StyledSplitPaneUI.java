/* $Id$ */
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
package games.stendhal.client.gui.styled;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * A SplitPaneUI implementation for drawing pixmap styled JSplitPanes.
 */
public class StyledSplitPaneUI extends BasicSplitPaneUI {
	private final Style style;

	// Required by UIManager
	public static ComponentUI createUI(JComponent pane) {
		// BasicScrollPaneUI instances can not be shared
		return new StyledSplitPaneUI(StyleUtil.getStyle());
	}

	/**
	 * Create a new StyledSplitPaneUI.
	 *
	 * @param style pixmap style
	 */
	public StyledSplitPaneUI(Style style) {
		this.style = style;
	}

	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
		return new StyledSplitPaneDivider(this, style);
	}

	@Override
	public void installUI(JComponent pane) {
		super.installUI(pane);
		pane.setBorder(style.getBorderDown());
	}

	/*
	 * BasicSplitPaneUI has a bug that the components' maximum sizes are
	 * ignored. The following overrides are a workaround to it.
	 */

	// part of the divider location bug workaround
	@Override
	public int getMaximumDividerLocation(JSplitPane pane) {
		int rightMax = super.getMaximumDividerLocation(pane);
		Component first = pane.getLeftComponent();
		if ((first != null) && first.isVisible()) {
			Dimension maxSize = first.getMaximumSize();
			Insets insets = pane.getInsets();
			if (pane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
				rightMax = Math.min(rightMax, maxSize.width + insets.left);
			} else {
				rightMax = Math.min(rightMax, maxSize.height + insets.top);
			}
			// Sanity check. Must be in this method, not in
			// getMinimumDividerLocation() (see below)
			rightMax = Math.max(rightMax, getMinimumDividerLocation(pane));
		}

		return rightMax;
	}

	// Another bug workaround. For whatever reason the left component is given
	// a preferred size that is too high by the amount of the divider width.
	@Override
	public Dimension getPreferredSize(JComponent comp) {
		Dimension rval = super.getPreferredSize(comp);
		if (getSplitPane().getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			rval.width -= getSplitPane().getDividerSize();
		} else {
			rval.height -= getSplitPane().getDividerSize();
		}
		return rval;
	}

	// part of the divider location bug workaround
	@Override
	public int getMinimumDividerLocation(JSplitPane pane) {
		int leftMin = super.getMinimumDividerLocation(pane);
		Component second = pane.getRightComponent();
		if ((second != null) && second.isVisible()) {
			Dimension paneSize = splitPane.getSize();
			Dimension maxSize = second.getMaximumSize();
			Insets insets = pane.getInsets();
			if (pane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
				leftMin = Math.max(leftMin, paneSize.width - insets.right - maxSize.width);
			} else {
				leftMin = Math.max(leftMin, paneSize.height - insets.bottom - maxSize.height);
			}
			/*
			 * To avoid inconsistency with the maximum location, it would seem
			 * reasonable to do:
			 *
			 *	leftMin = Math.min(leftMin, getMaximumDividerLocation(pane));
			 *
			 * however, the parent already calls getMinimumDividerLocation()
			 * in getMaximumDividerLocation(), so that would be a good way
			 * to get a stack overflow.
			 */
		}
		return leftMin;
	}

	// part of the divider location bug workaround
	@Override
	public void setDividerLocation(JSplitPane pane, int location) {
		// Override the stupidity
		int newLocation = Math.min(location, getMaximumDividerLocation(pane));
		newLocation = Math.max(newLocation, getMinimumDividerLocation(pane));
		if (newLocation != location) {
			pane.setDividerLocation(newLocation);
		} else {
			super.setDividerLocation(pane, location);
		}

		/*
		 * It seems that JSplitPane fails to set lastDividerLocation properly
		 * at component resizes if the divider location is too high for the new
		 * size. Set it here, so that after resizes getLastDividerLocation()
		 * returns the location before resizing, rather than the one before
		 * that. Needs to be pushed to the end of the event queue, because
		 * JSplitPane sets the last location after calling
		 * ui.setDividerLocation().
		 */
		final int lastLocation = newLocation;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getSplitPane().setLastDividerLocation(lastLocation);
			}
		});
	}

	// part of the divider location bug workaround
	@Override
	protected void dragDividerTo(int location) {
		// Override the stupidity
		location = Math.min(location, getMaximumDividerLocation(getSplitPane()));
		location = Math.max(location, getMinimumDividerLocation(getSplitPane()));
		super.dragDividerTo(location);
	}

	/**
	 * A split pane divider drawn with style.
	 */
	private static class StyledSplitPaneDivider extends BasicSplitPaneDivider {
		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = 3799692779880585757L;

		private final Style style;

		/**
		 * Create a new StyledSplitPaneDivider.
		 *
		 * @param ui UI delegate of the parent <code>JSplitPane</code>
		 * @param style drawing style
		 */
		public StyledSplitPaneDivider(StyledSplitPaneUI ui, Style style) {
			super(ui);
			this.style = style;
			addMouseListener(new DividerMouseListener(this));
		}

		// There's no paintComponent. This is an awt widget.
		@Override
		public void paint(Graphics g) {
			StyleUtil.fillBackground(style, g, 0, 0, getWidth(), getHeight());

			// Ribbing
			Insets insets = style.getBorder().getBorderInsets(this);
			if (splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
				int y = insets.top + 1;
				int maxY = getHeight() - insets.bottom - 1;
				while (y < maxY) {
					g.setColor(style.getShadowColor());
					g.drawLine(5, y, getWidth() - 6, y);
					y++;
					g.setColor(style.getHighLightColor());
					g.drawLine(5, y, getWidth() - 6, y);
					y++;
				}
			} else {
				int x = insets.left + 1;
				int maxX = getWidth() - insets.right - 1;
				while (x < maxX) {
					g.setColor(style.getShadowColor());
					g.drawLine(x, 5, x, getHeight() - 6);
					x++;
					g.setColor(style.getHighLightColor());
					g.drawLine(x, 5, x, getHeight() - 6);
					x++;
				}
			}

			// highlighting
			if (isMouseOver()) {
				highLightBorder(g);
			} else {
				paintBorder(g);
			}
		}

		/**
		 * Paint the handle using the normal border. (No highlighting)
		 *
		 * @param g graphics
		 */
		private void paintBorder(Graphics g) {
			int left = 0;
			int right = 0;
			int top = 0;
			int bottom = 0;
			Border border = style.getBorder();
			if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
				top = border.getBorderInsets(this).top;
				bottom = border.getBorderInsets(this).bottom;
			} else {
				left = border.getBorderInsets(this).left;
				right = border.getBorderInsets(this).right;
			}
			border.paintBorder(this, g, 0 - left, 0 - top,
				getWidth() + right + left, getHeight() + top + bottom);
		}

		/**
		 * Paint highlighted borders. Meanot to be used at mouseover.
		 *
		 * @param g graphics
		 */
		private void highLightBorder(Graphics g) {
			g.setColor(style.getHighLightColor());

			Border border = style.getBorder();
			if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
				int width = border.getBorderInsets(this).left;
				g.fillRect(0, 0, width, getHeight());
				g.fillRect(getWidth() - width, 0, width, getHeight());
			} else {
				int height = border.getBorderInsets(this).top;
				g.fillRect(0, 0, getWidth(), height);
				g.fillRect(0, getHeight() - height, getWidth(), height);
			}
		}

		/**
		 * Listener for mouse entering and leaving messages. Otherwise
		 * the divider does not get repainted at mouseover, unlike
		 * most other components.
		 */
		private static class DividerMouseListener extends MouseAdapter {
			private final StyledSplitPaneDivider divider;

			public DividerMouseListener(StyledSplitPaneDivider divider) {
				this.divider = divider;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				divider.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				divider.repaint();
			}
		}
	}
}
