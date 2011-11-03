/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.postman;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.Pair;

/**
 * EventRaiser keeps track of EventHandlers and dispatches events to them
 *
 * @author hendrik
 */
class EventRaiser {
	private static EventRaiser instance;

	private Map<Pair<EventType, String>, List<EventHandler>> waitingEventHandlers = new HashMap<Pair<EventType, String>, List<EventHandler>>();

	/**
	 * gets the EventRaiser
	 *
	 * @return EventRaiser
	 */
	public static EventRaiser get() {
		if (instance == null) {
			instance = new EventRaiser();
		}
		return instance;
	}

	/**
	 * adds an EventHandler to the list
	 *
	 * @param eventType   type of event to listen for
	 * @param eventDetail details of the event (e. g. for IRC_OP this is the channel name)
	 * @param eventHandler the handler to register
	 */
	public void addEventHandler(EventType eventType, String eventDetail, EventHandler eventHandler) {
		Pair<EventType, String> key = new Pair<EventType, String>(eventType, eventDetail);
		List<EventHandler> handlers = waitingEventHandlers.get(key);
		if (handlers == null) {
			handlers = new LinkedList<EventHandler>();
			waitingEventHandlers.put(key, handlers);
		}
		handlers.add(eventHandler);
	}

	/**
	 * fires and removes an event handler
	 *
	 * @param eventType   type of event to listen for
	 * @param eventDetail details of the event (e. g. for IRC_OP this is the channel name)
	 * @param furtherData additional data to pass to the event handler
	 */
	public void fire(EventType eventType, String eventDetail, String furtherData) {
		Pair<EventType, String> key = new Pair<EventType, String>(eventType, eventDetail);
		List<EventHandler> handlers = waitingEventHandlers.remove(key);
		if (handlers != null) {
			for(EventHandler handler : handlers) {
				handler.fire(eventType, eventDetail, furtherData);
			}
		}
	}
}
