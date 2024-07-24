/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.events.generic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import games.stendhal.client.entity.Entity;


/**
 * Sub-event for generic events.
 */
public abstract class SubEvent {

	/** List of enabled flags. */
	private final List<String> flags;


	/**
	 * Creates a new event.
	 *
	 * @param flags
	 *   List of enabled flags.
	 */
	public SubEvent(final String[] flags) {
		this.flags = Collections.unmodifiableList(Arrays.asList(flags));
	}

	/**
	 * Called when the event occurs.
	 *
	 * @param entity
	 *   Entity associated with event.
	 */
	public abstract void execute(Entity entity);

	/**
	 * Checks if a flag has been specified.
	 *
	 * @param flag
	 *   Name of flag checking for.
	 * @return
	 *   {@code true} if {@code flag} found in list of enabled flags.
	 */
	protected boolean flagEnabled(final String flag) {
		for (final String f: flags) {
			if (f.equals(flag)) {
				return true;
			}
		}
		return false;
	}
}
