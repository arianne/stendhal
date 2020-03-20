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

import org.apache.log4j.Logger;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.common.constants.Events;
import marauroa.common.game.RPEvent;

/**
 * creates RPEvent handler
 *
 * @author hendrik
 */
class EventFactory {
	private static final Logger logger = Logger.getLogger(EventFactory.class);

	/**
	 * creates events for Entities and RPEntities
	 * @param entity
	 * @param rpevent
	 * @return event
	 */
	static Event<? extends Entity> create(Entity entity, RPEvent rpevent) {
		Event<? extends Entity> res = null;
		if (entity instanceof RPEntity) {
			res = createEventsForRPEntity((RPEntity)entity, rpevent);
		}

		if (res == null) {
			res = createEventsForEntity(entity, rpevent);
		}

		if (res == null) {
			Event<Entity> unknown = new UnknownEvent<Entity>();
			unknown.init(entity, rpevent);
			res = unknown;
		}
		logger.debug("Created event: "+res);
		return res;
	}

	/**
	 * Creates events for normal RPEntities.
	 *
	 * @param entity  RPEntityEntity
	 * @param rpevent RPEvent
	 * @return Event handler
	 */
	private static Event<RPEntity> createEventsForRPEntity(RPEntity entity, RPEvent rpevent) {
		String name = rpevent.getName();
		Event<RPEntity> event = null;
		if (name.equals(Events.PUBLIC_TEXT)) {
			event = new PublicTextEvent();
		} else if (name.equals(Events.PRIVATE_TEXT)) {
			event = new PrivateTextEvent();
		} else if (name.equals(Events.ATTACK)) {
			event = new AttackEvent();
		} else if (name.equals(Events.TRADE_STATE_CHANGE)) {
			event = new TradeStateChangeEvent();
		} else if (name.equals(Events.GROUP_CHANGE)) {
			event = new GroupChangeEvent();
		} else if (name.equals(Events.GROUP_INVITE)) {
			event = new GroupInviteEvent();
		} else if (name.equals(Events.PROGRESS_STATUS_CHANGE)) {
			event = new ProgressStatusEvent();
		} else if (name.equals(Events.REACHED_ACHIEVEMENT)) {
			event = new ReachedAchievementEvent();
		} else if (name.equals(Events.BESTIARY)) {
			event = new BestiaryEvent();
		}

		if (event != null) {
			event.init(entity, rpevent);
		}
		return event;
	}

	/**
	 * Creates events for normal Entities.
	 *
	 * @param entity  Entity
	 * @param rpevent RPEvent
	 * @return Event handler
	 */
	private static Event<Entity> createEventsForEntity(Entity entity, RPEvent rpevent) {
		String name = rpevent.getName();
		Event<Entity> event = null;

		if (name.equals("examine")) {
			event = new ExamineEvent();
		} else if (name.equals("show_item_list")) {
			event = new ShowItemListEvent();
		} else if (name.equals(Events.OUTFIT_LIST)) {
			event = new ShowOutfitListEvent();
		} else if (name.equals(Events.SOUND)) {
			event = new SoundEvent();
		} else if (name.equals("transition_graph")) {
			event = new TransitionGraphEvent();
		} else if (name.equals(Events.PLAYER_LOGGED_ON)) {
			event = new PlayerLoggedOnEvent();
		} else if (name.equals(Events.PLAYER_LOGGED_OUT)) {
			event = new PlayerLoggedOutEvent();
		} else if (name.equals(Events.VIEW_CHANGE)) {
			event = new ViewChangeEvent();
		}  else if (name.equals(Events.IMAGE)) {
			event = new ImageEffectEvent();
		} else if (name.equals(Events.PUBLIC_TEXT)) {
			event = new EntityMessageEvent();
		}  else if (name.equals(Events.GLOBAL_VISUAL)) {
			event = new GlobalVisualEffectEvent();
		}

		if (event != null) {
			event.init(entity, rpevent);
		}
		return event;
	}
}
