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
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
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
			optionPane.setOpaque(false);
			// Same for the parent container
			Container parent = optionPane.getParent();
			if (parent instanceof JComponent) {
				((JComponent) parent).setOpaque(false);
				/*
				 * This is a workaround for setOpaque not working correctly in
				 * java 1.5 if the background color is not set. The color does
				 * not actually matter, as it gets never drawn.
				 */
				((JComponent) parent).setBackground(style.getPlainColor());
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

	@Override
	protected void addMessageComponents(Container container,
		GridBagConstraints cons, Object msg, int maxll,
		boolean internallyCreated) {
		/*
		 * A workaround to the well known flaw in JLabel that the text
		 * can not be made selectable. Use a non editable JTextField
		 * instead with the defaults of JLabel.
		 */
		 if (msg instanceof String) {
			 JTextArea text = new JTextArea((String) msg);
			 text.setEditable(false);
			 text.setBorder(null);
			 text.setOpaque(false);
			 text.setForeground(style.getForeground());
			 text.setFont(UIManager.getFont("Label.font"));

			 msg = text;
		 }
		 super.addMessageComponents(container, cons, msg, maxll, internallyCreated);
	}
}
