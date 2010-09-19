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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

/**
 * OptionPaneUI that tries to clean a bit of the assumptions that everyone uses
 * the metal theme.
 */
public class StyledOptionPaneUI extends BasicOptionPaneUI {
	/*
	 * It would be probably easier to reimplement the OptionPaneUI from scratch
	 * that trying to clean the basic implementation that is tied to the core
	 * to the metal version. That would be problematic for testing the code,
	 * unfortunately. OptionPane does everything, and it's hard to check if all
	 * the use cases are covered.
	 * 
	 * The component abuses panels everywhere, and that just does not work at
	 * all with patterned backgrounds. The tiling errors are visible, but
	 * hopefully users do not encounter the dialogs too often for it to bother.
	 */
	private final Style style;
	private boolean cleaned;
	
	/**
	 * Create a new StyledOptionPaneUI. This method is used by the UIManager.
	 * 
	 * @param pane a JOptionpane
	 * @return StyledOptionPaneUI for pane
	 */
	public static ComponentUI createUI(JComponent pane) {
		return new StyledOptionPaneUI(StyleUtil.getStyle());
	}
	
	public StyledOptionPaneUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void paint(Graphics g, JComponent optionPane) {
		/*
		 * There does not seem to be any better place for this. Everywhere else
		 * its too early to actually change the settings.
		 */
		if (!cleaned) {
			cleanComponents(optionPane);
			/*
			 * Set the color to transparent so that the background panel shows
			 * through.
			 */
			Color transparent = new Color(0.0f, 0.0f, 0.0f, 0.0f);
			optionPane.setBackground(transparent);
			
			Container parent = optionPane.getParent();
			if (parent instanceof JComponent) {
				/*
				 * Totally confusingly the color does not seem to make any 
				 * difference, as long as it's set to something. If it's not
				 * changed it gets painted in gray.
				 *
				 * Setting it to something sane anyway, in case the result is
				 * different in some swing versions.
				 */
				((JComponent) parent).setBackground(transparent);
			}
			
			cleaned = true;
		}
	}
	
	/**
	 * Try to clean a component and its children of the settings where it 
	 * assumes metal theme. BasicOptionPaneUI does not provide any access to
	 * most of its sub components, so this seems to be the only workable way.
	 * 
	 * @param component the component where to start cleaning
	 */
	private void cleanComponents(Component component) {
		if (component instanceof Container) {
			if (component instanceof JPanel) {
				((JPanel) component).setBorder(null);
			} else if (component instanceof JLabel) {
				// BasicOptionPaneUI insists on using hard coded grey for text.
				// Not a very good choice for dark backgrounds.
				component.setForeground(style.getForeground());
			}
			
			for (Component child : ((Container) component).getComponents()) {
				cleanComponents(child);
			}
		}
	}
}
