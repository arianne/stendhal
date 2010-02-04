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
