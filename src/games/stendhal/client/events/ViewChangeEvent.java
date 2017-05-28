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
package games.stendhal.client.events;

import javax.swing.SwingUtilities;

import games.stendhal.client.GameScreen;
import games.stendhal.client.entity.Entity;

/**
 * View center changing event.
 */
class ViewChangeEvent extends Event<Entity> {
	@Override
	public void execute() {
		final int x = event.getInt("x");
		final int y = event.getInt("y");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GameScreen.get().positionChanged(x, y);
			}
		});
	}
}
