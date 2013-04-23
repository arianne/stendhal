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
package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.admin.TransitionDiagram;

/**
 * shows the transition diagram of a finite state machine.
 *
 * @author hendrik
 */
class TransitionGraphEvent extends Event<Entity> {
	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		new TransitionDiagram().showTransitionDiagram(event.get("data"));
	}
}
