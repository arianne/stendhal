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
package games.stendhal.server.maps.quests.revivalweeks;

/**
 * This interface represents content that can be loaded and unloaded at runtime.
 *
 * @author hendrik
 */
public interface LoadableContent {

	/**
	 * adds the content to the world.
	 */
	public void addToWorld();

	/**
	 * try to remove the content from the world-
	 *
	 * @return <code>true</code>, if the content was removed, <code>false</code> otherwise
	 */
	public boolean removeFromWorld();
}
