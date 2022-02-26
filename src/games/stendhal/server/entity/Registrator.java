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
package games.stendhal.server.entity;

import org.apache.log4j.Logger;

import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;


/**
  * Wrapper for java Observable object.
  */
public class Registrator extends Observable {

	/**
	 * registers observer for notifying
	 * @param observer
	 * 			- observer to add
	 */
	public void setObserver(final Observer observer) {
		if(observer != null) {
			addObserver(observer);
		} else {
			// log it.
			final Logger logger = Logger.getLogger(Registrator.class);
			logger.error("null observer was not added.", new Throwable());
		}
	}

	/**
	 * remove observer from observers list.
	 * @param observer
	 * 			- observer to remove.
	 */
	public void removeObserver(final Observer observer) {
		deleteObserver(observer);
	}

}
