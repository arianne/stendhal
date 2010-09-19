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

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
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
	
	/**
	 * A split pane divider drawn with style.
	 */
	private static class StyledSplitPaneDivider extends BasicSplitPaneDivider {
		private final Style style;
		
		/**
		 * Create a new StyledSplitPaneDivider.
		 * 
		 * @param style drawing style
		 */
		public StyledSplitPaneDivider(StyledSplitPaneUI ui, Style style) {
			super(ui);
			this.style = style;
			addMouseListener(new DividerMouseListener(this));
		}
		
		@Override
		public void paint(Graphics g) {
			// There's no paintComponent. This is an awt widget.
			StyleUtil.fillBackground(style, g, 0, 0, getWidth(), getHeight());
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
		private static class DividerMouseListener implements MouseListener {
			private final StyledSplitPaneDivider divider;
			
			public DividerMouseListener(StyledSplitPaneDivider divider) {
				this.divider = divider;
			}
			
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				divider.repaint();
			}

			public void mouseExited(MouseEvent e) {
				divider.repaint();
			}
			
			public void mousePressed(MouseEvent e) {
			}
			
			public void mouseReleased(MouseEvent e) {
			}
		}
	}
}
