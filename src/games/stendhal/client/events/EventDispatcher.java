package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;

/**
 * dispatches all events to the appropriate event handler class.
 *
 * @author hendrik
 */
public class EventDispatcher {

	/**
	 * dispatches events
	 *
	 * @param rpobject RPObject with events
	 * @param entity Entity
	 */
	public static void dispatchEvents(final RPObject rpobject, Entity entity) {
		for (final RPEvent rpevent : rpobject.events()) {
			Event<? extends Entity> event = EventFactory.create(entity, rpevent);
			event.execute();
		}
	}
}
