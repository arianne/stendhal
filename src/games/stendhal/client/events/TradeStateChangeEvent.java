package games.stendhal.client.events;

import games.stendhal.client.entity.RPEntity;

import org.apache.log4j.Logger;

/**
 * adjust the player to player trade state
 *
 * @author hendrik
 */
public class TradeStateChangeEvent extends Event<RPEntity> {
	private static Logger logger = Logger.getLogger(TradeStateChangeEvent.class);

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		logger.info("Trade event: " + event);
	}

}
