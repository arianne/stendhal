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

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 * UI delegate for JSpinners
 */
public class StyledSpinnerUI extends BasicSpinnerUI {
	private final Style style;

	// Required by UIManager
	public static ComponentUI createUI(JComponent pane) {
		// BasicSpinnerUI instances can not be shared
		return new StyledSpinnerUI(StyleUtil.getStyle());
	}

	/**
	 * Create a new pixmap style.
	 *
	 * @param style {@link Style} to be used for drawing the spinner
	 */
	public StyledSpinnerUI(Style style) {
		this.style = style;
	}

	@Override
	protected JComponent createEditor() {
		JComponent editor = super.createEditor();
		editor.setBorder(style.getBorderDown());
		/*
		 * BasicSpinnerUI likes to create borders within borders; for some
		 * reason the editor is *not* a TextField, but some sort of a container
		 * that holds the text field. Remove any spurious borders.
		 */
		for (Component child : editor.getComponents()) {
			if (child instanceof JComponent) {
				((JComponent) child).setBorder(null);
			}
		}

		return editor;
	}

	@Override
	protected Component createNextButton() {
		Component button = new StyledArrowButton(SwingConstants.NORTH, style);
		installNextButtonListeners(button);
		return button;
	}

	@Override
	protected Component createPreviousButton() {
		Component button = new StyledArrowButton(SwingConstants.SOUTH, style);
		installPreviousButtonListeners(button);
		return button;
	}

	@Override
	public void installUI(JComponent spinner) {
		super.installUI(spinner);
		spinner.setBorder(null);
	}
}
