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

import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

/**
 * A builder for AtttributedStrings, that works quite similarly to StringBuilder.
 */
public class AttributedStringBuilder implements AttributedTextSink<TextAttributeSet> {
	/** Builder for the plain string representation. */
	private final StringBuilder stringBuilder = new StringBuilder();
	/** Attributes and their positions to be added to the finished string. */
	private final List<AttributeDefinition> attributeList = new ArrayList<AttributeDefinition>();

	@Override
	public String toString() {
		return stringBuilder.toString();
	}

	/**
	 * Append a string with attributes.
	 *
	 * @param s string
	 * @param attrs attributes to be used for the appended part
	 */
	@Override
	public void append(String s, TextAttributeSet attrs) {
		int beginIndex = stringBuilder.length();
		int endIndex = beginIndex + s.length();
		stringBuilder.append(s);

		attributeList.add(new AttributeDefinition(attrs, beginIndex, endIndex));
	}

	/**
	 * Get the built AttributedString.
	 *
	 * @return AttributedString with the appended parts and attributes that have
	 *	been defined for those at the time of appending
	 */
	public AttributedString toAttributedString() {
		AttributedString rval = new AttributedString(toString());

		for (AttributeDefinition def : attributeList) {
			def.apply(rval);
		}

		return rval;
	}

	/**
	 * Holder for attributes and their locations.
	 */
	private static class AttributeDefinition {
		/** Attribute definitions for the range. */
		private final TextAttributeSet attrs;
		/** Range indices for the attributes. */
		private final int beginIndex, endIndex;

		/**
		 * Create new AttributeDefinition.
		 * @param attrs attribute values
		 * @param beginIndex start of the range
		 * @param endIndex end of the range
		 */
		AttributeDefinition(TextAttributeSet attrs, int beginIndex, int endIndex) {
			this.attrs = attrs;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
		}

		/**
		 * Apply the attributes to the appropriate range of an AttributedString.
		 *
		 * @param str string to be annotated with the attributes
		 */
		void apply(AttributedString str) {
			str.addAttributes(attrs.contents(), beginIndex, endIndex);
		}
	}
}
