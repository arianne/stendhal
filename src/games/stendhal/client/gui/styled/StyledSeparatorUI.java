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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * Stendhal look and feel for JSeparators.
 */
public class StyledSeparatorUI extends BasicSeparatorUI {
	/** Shared UI instance */
	private static StyledSeparatorUI instance;
	/** Used style */
	private final Style style;

	/**
	 * Create StyledSeparatorUI for a separator.
	 *
	 * @param separator <code>JSeparator</code> to create an UI for
	 * @return a ComponentUI instance
	 */
	// required by UIManager. Not necessarily called from on thread
	public static synchronized ComponentUI createUI(JComponent separator) {
		// Separator UIs can be shared
		if (instance == null) {
			instance = new StyledSeparatorUI(StyleUtil.getStyle());
		}

		return instance;
	}

	/**
	 * Create a new StyledSeparatorUI.
	 *
	 * @param style pixmap style
	 */
	public StyledSeparatorUI(Style style) {
		this.style = style;
	}

	@Override
	public void installUI(JComponent separator) {
		super.installUI(separator);
		separator.setBackground(style.getHighLightColor());
		separator.setForeground(style.getShadowColor());
	}
}
