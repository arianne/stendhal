package games.stendhal.client.events;

import org.apache.log4j.Logger;

/**
 * an unknown event
 *
 * @author hendrik
 */
public class UnknownEvent extends Event {
	private static Logger logger = Logger.getLogger(UnknownEvent.class);

	@Override
	public void execute() {
		logger.warn("Received unknown event: " + event + " on entity " + entity);
	}

}
