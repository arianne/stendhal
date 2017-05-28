/***************************************************************************
 *                  (C) Copyright 2003 - 2015 Faiumoni e.V.                *
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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/**
 * Special document filter for editing number only content.
 */
public class NumberDocumentFilter extends DocumentFilter {
	/** Text component where the editing is done. */
	private final JTextComponent comp;
	/** Flag for inserting 0 on empty documents. */
	private final boolean defaultZero;

	/**
	 * Create a filter for a text component.
	 *
	 * @param comp text component. This is only used to highlight the "0"
	 * 	when the user deletes all other text
	 * @param defaultZero if <code>true</code>, then on clearing the document,
	 *	a preselected 0 is inserted.
	 */
	public NumberDocumentFilter(JTextComponent comp, boolean defaultZero) {
		this.comp = comp;
		this.defaultZero = defaultZero;
	}

	@Override
	public void replace(FilterBypass fb, int offset,
			int len, String str, AttributeSet a) throws BadLocationException {
		// Allow inserting only numbers
		str = str.replaceAll("\\D", "");
		if (!str.isEmpty()) {
			super.replace(fb, offset, len, str, a);
		}
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);
		if (defaultZero && fb.getDocument().getLength() == 0) {
			// Just deleted the entire contents. Place a 0
			// there that will get automatically overwritten
			// at new edits
			fb.insertString(0, "0", null);
			comp.selectAll();
		}
	}
}
