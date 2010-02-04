package games.stendhal.client.events;

import games.stendhal.client.entity.RPEntity;


/**
 * Private chat
 *
 * @author hendrik
 */
public class PrivateTextEvent extends Event<RPEntity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		entity.onPrivateListen(event.get("texttype"), event.get("text"));
	}

}
