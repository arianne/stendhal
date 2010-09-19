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
import marauroa.common.game.RPEvent;

/**
 * abstract parent class for client side event handling
 *
 * @author hendrik
 * @param <T> entity
 */
public abstract class Event<T extends Entity> {
	protected T entity;
	protected RPEvent event;

	/**
	 * initializes the event
	 *
	 * @param entity the Entity which caused the event
	 * @param event RPEvent
	 */
	public void init(T entity, RPEvent event) {
		this.entity = entity;
		this.event = event;
	}

	/**
	 * executes the event
	 */
	public abstract void execute();
}
