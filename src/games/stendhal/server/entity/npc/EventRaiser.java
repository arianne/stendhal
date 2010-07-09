/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2010 - Stendhal                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.RPEvent;

/**
 * the source that raised an event
 *
 * @author hendrik
 */
public class EventRaiser {
	private Entity entity;

	/**
	 * creates a new EventRaiser
	 *
	 * @param entity Entity
	 */
	public EventRaiser(Entity entity) {
		this.entity = entity;
	}

	/**
	 * gets the RPEntity the SpeakerNPC is attenting to
	 *
	 * @return RPEntity or <code>null</code>
	 */
	public RPEntity getAttending() {
		if (entity instanceof SpeakerNPC) {
			return ((SpeakerNPC) entity).getAttending();
		}
		return null;
	}

	/**
	 * lets the SpeakerNPC say a sentence
	 *
	 * @param sentence to say
	 */
	public void say(String sentence) {
		if (entity instanceof SpeakerNPC) {
			((SpeakerNPC) entity).say(sentence);
		}
	}

	/**
	 * gets the Entity
	 *
	 * @return Entity
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * gets the name of the entity
	 *
	 * @return name
	 */
	public String getName() {
		if (entity instanceof RPEntity) {
			return ((RPEntity) entity).getName();
		} else {
			return entity.get("name");
		}
		
	}

	/**
	 * sets the sate of the FSM used by SpeakerNPCs
	 *
	 * @param stateAfterCompletion new state
	 */
	public void setCurrentState(ConversationStates stateAfterCompletion) {
		if (entity instanceof SpeakerNPC) {
			((SpeakerNPC) entity).setCurrentState(stateAfterCompletion);
		}
	}

	/**
	 * adds an RPEvent
	 *
	 * @param event event to add
	 */
	public void addEvent(RPEvent event) {
		entity.addEvent(event);
	}

	/**
	 * gets the x-coordinate
	 *
	 * @return x
	 */
	public int getX() {
		return entity.getX();
	}

	/**
	 * gets the y-coordinate
	 *
	 * @return y
	 */
	public int getY() {
		return entity.getY();
	}

	/**
	 * gets the zone of the entity
	 *
	 * @return StendhalRPZone
	 */
	public StendhalRPZone getZone() {
		return entity.getZone();
	}

}
