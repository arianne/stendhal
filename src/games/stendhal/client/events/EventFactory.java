package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.common.constants.Events;
import marauroa.common.game.RPEvent;

import org.apache.log4j.Logger;

/**
 * creates RPEvent handler
 *
 * @author hendrik
 */
public class EventFactory {
	
	private static final Logger logger = Logger.getLogger(EventFactory.class);

	/**
	 * creates events for Entities and RPEntities
	 * @param entity
	 * @param rpevent
	 * @return event
	 */
	public static Event<? extends Entity> create(Entity entity, RPEvent rpevent) {
		Event<? extends Entity> res = null;
		if (entity instanceof RPEntity) {
			res = createEventsForRPEntity(entity, rpevent);
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
	 * creates events for normal RPEntities
	 *
	 * @param entity  RPEntityEntity
	 * @param rpevent RPEvent
	 * @return Event handler
	 */
	private static Event<RPEntity> createEventsForRPEntity(Entity entity, RPEvent rpevent) {
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
		}

		if (event != null) {
			event.init((RPEntity) entity, rpevent);
		}
		return event;
	}

	/**
	 * creates events for normal Entities
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
		} else if (name.equals(Events.SOUND)) {
			event = new SoundEvent();
		} else if (name.equals("transition_graph")) {
			event = new TransitionGraphEvent();
		} else if (name.equals(Events.PLAYER_LOGGED_ON)) {
			event = new PlayerLoggedOnEvent();
		} else if (name.equals(Events.PLAYER_LOGGED_OUT)) {
			event = new PlayerLoggedOutEvent();
		}

		if (event != null) {
			event.init(entity, rpevent);
		}
		return event;
	}
}
