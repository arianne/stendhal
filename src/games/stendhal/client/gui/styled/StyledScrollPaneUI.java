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
import javax.swing.plaf.basic.BasicScrollPaneUI;

/**
 * A style for a scroll pane. Just changes the default border.
 */
public class StyledScrollPaneUI extends BasicScrollPaneUI {
	private final Style style;

	// Required by UIManager
	public static ComponentUI createUI(JComponent pane) {
		// BasicScrollPaneUI instances can not be shared
		return new StyledScrollPaneUI(StyleUtil.getStyle());
	}

	/**
	 * Create a new pixmap style.
	 *
	 * @param style {@link Style} to be used for drawing the panel
	 */
	public StyledScrollPaneUI(Style style) {
		this.style = style;
	}

	@Override
	public void installUI(JComponent pane) {
		super.installUI(pane);
		pane.setBorder(style.getBorderDown());
	}
}
