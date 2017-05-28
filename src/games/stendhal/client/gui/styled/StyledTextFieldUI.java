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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

/**
 * TextUI using pixmap styles for JTextFields.
 */
public class StyledTextFieldUI extends BasicTextFieldUI {
	/** Pixels before the first letter and after the last */
	private static final int PADDING = 2;

	private final Style style;

	// Required by UIManager
	public static ComponentUI createUI(JComponent field) {
		// Text field UIs can not be shared
		return new StyledTextFieldUI(StyleUtil.getStyle());
	}

	/**
	 * Create a new StyledTextFieldUI.
	 *
	 * @param style pixmap style for drawing the borders
	 */
	public StyledTextFieldUI(Style style) {
		this.style = style;
	}

	@Override
	public void installUI(JComponent button) {
		super.installUI(button);
		button.setBorder(BorderFactory.createCompoundBorder(style.getBorderDown(),
				BorderFactory.createEmptyBorder(0, PADDING, 0, PADDING)));
	}
}
