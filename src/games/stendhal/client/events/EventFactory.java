package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import marauroa.common.game.RPEvent;

/**
 * creates RPEvent handler
 *
 * @author hendrik
 */
public class EventFactory {

	public static Event create(Entity entity, RPEvent rpevent) {
		Event event = new UnknownEvent();
		
		event.init(entity, rpevent);
		return event;
	}
}
