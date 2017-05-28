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

import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

/**
 * FormatSet implementation for TextAttribute maps.
 */
public class TextAttributeSet implements FormatSet<Map<TextAttribute, Object>, TextAttributeSet> {
	/** Map for storing the attributes. */
	private final Map<TextAttribute, Object> attributes;

	/**
	 * Create a new TextAttributeSet with no defined attributes.
	 */
	public TextAttributeSet() {
		attributes = new HashMap<TextAttribute, Object>();
	}

	/**
	 * Create a new TextAttributeSet with specified contents.
	 *
	 * @param initialContents initial attributes
	 */
	private TextAttributeSet(Map<TextAttribute, Object> initialContents) {
		attributes = new HashMap<TextAttribute, Object>(initialContents);
	}

	@Override
	public TextAttributeSet union(TextAttributeSet additional) {
		TextAttributeSet rval = new TextAttributeSet(attributes);
		rval.attributes.putAll(additional.contents());

		return rval;
	}

	@Override
	public TextAttributeSet copy() {
		return new TextAttributeSet(attributes);
	}

	@Override
	public Map<TextAttribute, Object> contents() {
		return attributes;
	}

	/**
	 * Set a specific attribute.
	 *
	 * @param attr attribute
	 * @param value new value of the attribute
	 */
	public void setAttribute(TextAttribute attr, Object value) {
		attributes.put(attr, value);
	}
}
