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

/**
 * Interface for destinations that can consume styled text.
 *
 * @param <T> used Text format
 */
public interface AttributedTextSink<T extends FormatSet<?, ?>> {
	/**
	 * Append a string with specified formatting.
	 *
	 * @param s appended string
	 * @param attrs format attributes
	 */
	void append(String s, T attrs);
}
