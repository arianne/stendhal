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
package games.stendhal.client;

import marauroa.common.game.RPObject;

/**
 * Convenience class with empty implemetation of ObjectChangeListener.
 */
final class ObjectChangeListenerAdapter implements ObjectChangeListener {

	/**
	 * is called when object is deleted.
	 *
	 * In addition to real deletion this happens on every zone change.
	 */
	@Override
	public void deleted() {
		// do nothing
	}

	/**
	 * is called when object got additional attributes (or values got changed ?).
	 *
	 */
	@Override
	public void modifiedAdded(final RPObject changes) {
		// do nothing
	}

	/**
	 * is called when attributes got deleted.
	 */
	@Override
	public void modifiedDeleted(final RPObject changes) {
		// do nothing
	}
}
