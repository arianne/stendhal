/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.textformat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.Style;
import javax.swing.text.StyleContext;

/**
 * FormatSet wrapper for Styles.
 */
public class StyleSet implements FormatSet<Style, StyleSet> {
	/** Style context. Required for using NamedStyles. */
	private final StyleContext sc;
	/** Style represented by this StyleSet. */
	private final Style style;
	/**
	 * A Workaround. Styles have extra junk that we haven't added. Avoid
	 * overwriting the things from the base StyleSet in union() by storing
	 * the values we have actually changed.
	 */
	private final Map<Object, Object> modified = new HashMap<Object, Object>();

	/**
	 * Create a new StyleSet.
	 *
	 * @param sc style context
	 * @param style wrapped style
	 */
	public StyleSet(StyleContext sc, Style style) {
		this.sc = sc;
		this.style = sc.new NamedStyle();
		this.style.addAttributes(style);
	}

	@Override
	public StyleSet union(StyleSet additional) {
		StyleSet rval = copy();
		for (Entry<Object, Object> entry : additional.modified.entrySet()) {
			rval.setAttribute(entry.getKey(), entry.getValue());
		}

		return rval;
	}

	@Override
	public StyleSet copy() {
		return new StyleSet(sc, style);
	}

	@Override
	public Style contents() {
		return style;
	}

	/**
	 * Set a text attribute. See {@link javax.swing.text.StyleConstants} for
	 * valid keys and values.
	 *
	 * @param key attribute key
	 * @param value attribute value
	 */
	public void setAttribute(Object key, Object value) {
		modified.put(key, value);
		style.addAttribute(key, value);
	}
}
