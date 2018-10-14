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
package games.stendhal.client.entity;

import games.stendhal.common.NotificationType;

/**
 * Data container for the text floaters.
 */
public final class TextIndicator {
	/**
	 * The age of the message (in ms).
	 */
	private int age;

	/**
	 * The message text.
	 */
	private final String text;

	/**
	 * The indicator type.
	 */
	private final NotificationType type;

	/**
	 * Create a floating message.
	 *
	 * @param text
	 *            The text to drawn.
	 * @param type
	 *            The indicator type.
	 */
	TextIndicator(final String text, final NotificationType type) {
		this.text = text;
		this.type = type;
	}

	//
	// TextIndicator
	//

	/**
	 * Add to the age of this message.
	 *
	 * @param time
	 *            The amout to add.
	 *
	 * @return The new age (in milliseconds).
	 */
	int addAge(final int time) {
		age += time;

		return age;
	}

	/**
	 * Get the age of this message.
	 *
	 * @return The age (in milliseconds).
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Get the text message.
	 *
	 * @return The text message.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Get the indicator type.
	 *
	 * @return The indicator type.
	 */
	public NotificationType getType() {
		return type;
	}
}
