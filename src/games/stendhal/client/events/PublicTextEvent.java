package games.stendhal.client.events;

import games.stendhal.client.entity.RPEntity;


/**
 * public chat and creature (text) noise.
 *
 * @author hendrik
 */
public class PublicTextEvent extends Event<RPEntity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		entity.onTalk(event.get("text"));
	}

}
