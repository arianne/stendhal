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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class StyledComboBoxUI extends BasicComboBoxUI {
	private final Style style;

	// Required by UIManager
	public static ComponentUI createUI(JComponent menuItem) {
		// BasicComboBoxUI can not be shared
		return new StyledComboBoxUI(StyleUtil.getStyle());
	}

	/**
	 * Create a new SytledComboBoxUI.
	 *
	 * @param style pixmap style
	 */
	public StyledComboBoxUI(Style style) {
		this.style = style;
	}

	@Override
	protected JButton createArrowButton() {
		return new StyledArrowButton(SwingConstants.SOUTH, style);
	}

	@Override
	protected ListCellRenderer<Object> createRenderer() {
		/*
		 * In java 6 the transparency setting of the entries gets
		 * overridden by StyledLabelUI. (It works ok in java 1.5).
		 */
		return new StyledComboBoxRenderer();
	}


	@Override
	public void installUI(JComponent component) {
		super.installUI(component);
		component.setBorder(style.getBorderDown());
		listBox.setSelectionBackground(style.getShadowColor());
		listBox.setSelectionForeground(style.getForeground());
	}

	/**
	 * A ListCellRenderer that returns opaque labels.
	 */
	private static class StyledComboBoxRenderer extends BasicComboBoxRenderer {
		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			// I doubt it can be anything but a JLabel, but better to be safe
			if (c instanceof JComponent) {
				JComponent label = (JComponent) c;
				label.setOpaque(true);
			}

			return c;
		}
	}
}
