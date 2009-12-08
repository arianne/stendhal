package games.stendhal.server.maps.semos.tavern.market;

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Grammar;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
import games.stendhal.server.util.TimeUtil;

public class OfferExpirer implements TurnListener{
	private static Logger logger = Logger.getLogger(OfferExpirer.class);
	/**
	 * number of days after which an offer will expire after warning.
	 */
	//private static final int DAYS_TO_EXPIRING = 3;
	/**
	 * number of days after which an offerer gets the expire warning.
	 */
	//private static final int DAYS_TO_WARNING = 4;
	/**
	 * number of days after which an expired offer will be removed completely.
	 */
	//private static final int DAYS_TO_REMOVING = 7;
	
	/**
	 * Total time in seconds before sending the player a warning.
	 */
	private static final int TIME_TO_WARNING = MathHelper.SECONDS_IN_ONE_HOUR;
	// DAYS_TO_WARNING * MathHelper.SECONDS_IN_ONE_DAY;
	/**
	 * Total time in seconds before expiring an offer.
	 */
	private static final int TIME_TO_EXPIRING = MathHelper.SECONDS_IN_ONE_HOUR * 2;
	// (DAYS_TO_WARNING + DAYS_TO_EXPIRING) * MathHelper.SECONDS_IN_ONE_DAY;
	/**
	 * Total time in seconds before removing an offer completely. 
	 */
	private static final int TIME_TO_REMOVING = MathHelper.SECONDS_IN_ONE_HOUR * 4;
	// (DAYS_TO_WARNING + DAYS_TO_EXPIRING + DAYS_TO_REMOVING) * MathHelper.SECONDS_IN_ONE_DAY;
	private long timeStamp = 0;
	
	/**
	 * Time between checks in seconds.
	 */
	private static final int CHECKING_INTERVAL = MathHelper.SECONDS_IN_ONE_MINUTE;
	// MathHelper.SECONDS_IN_ONE_HOUR;
	
	private Market market;
	
	public OfferExpirer(Market market) {
		this.market = market;
		
		TurnNotifier.get().notifyInSeconds(CHECKING_INTERVAL, this);
	}

	public void onTurnReached(int currentTurn) {
		runChecksAndRestart();
	}
	
	/**
	 * Run the expiration checks and restart the timer.
	 */
	private void runChecksAndRestart() {
		// Check the expired offers first, to avoid sending warning messages 
		// about offers that are expired too. 
		checkExpired();
		checkWarnings();
		checkRemoved();
		
		TurnNotifier.get().notifyInSeconds(CHECKING_INTERVAL, this);
	}
	
	private void sendMessage(String player, StringBuilder message) {
		// A bit backward way to prepend to the message
		message.insert(0, " ");
		message.insert(0, player);
		message.insert(0, "Harold tells you: tell ");
		
		Player postman = SingletonRepository.getRuleProcessor().getPlayer("postman");
		if (postman != null) { 
			postman.sendPrivateText(message.toString());
		} else {
			message.insert(0, "Could not use postman for the following message: ");
			logger.warn(message.toString());
		}
	}
	
	/**
	 * Check for offers that should be expired.
	 */
	private void checkExpired() {
		List<Offer> list = market.getOffersOlderThan(TIME_TO_EXPIRING);
		
		for (Offer offer : list) {
			market.expireOffer(offer);
			StringBuilder builder = new StringBuilder();
			builder.append("Your offer of ");
			builder.append(Grammar.a_noun(offer.getItem().getName()));
			builder.append(" has expired. You have ");
			builder.append(TimeUtil.approxTimeUntil((int) ((offer.getTimestamp() 
					- System.currentTimeMillis() + 1000 * TIME_TO_REMOVING) / 1000)));
			builder.append(" left to get the item back or prolong the offer.");
			sendMessage(offer.getOfferer(), builder);
		}
	}
	
	/**
	 * Check for offers that should be permanently removed
	 */
	private void checkRemoved() {
		List<Offer> list = market.getExpiredOffersOlderThan(TIME_TO_REMOVING);
		
		for (Offer offer : list) {
			market.removeExpiredOffer(offer);
			StringBuilder builder = new StringBuilder();
			builder.append("Your offer of ");
			builder.append(Grammar.a_noun(offer.getItem().getName()));
			builder.append(" has been removed permanently from the market.");
			sendMessage(offer.getOfferer(), builder);
		}
	}
	
	private void checkWarnings() {
		List<Offer> list = market.getOffersOlderThan(TIME_TO_WARNING);
		
		long time = System.currentTimeMillis();
		for (Offer offer : list) {
			// Send the warning only once, unless the server has been 
			// restarted since the last check. In that case, message the 
			// player always to be sure that she gets at least one warning.
			long timeDiff = time - timeStamp;
			if (timeStamp != 0 && ((time - offer.getTimestamp() - TIME_TO_WARNING * 1000) > timeDiff)) {
				continue;
			}
			
			StringBuilder builder = new StringBuilder();
			builder.append("Your offer of ");
			builder.append(Grammar.a_noun(offer.getItem().getName()));
			builder.append(" will expire in ");
			builder.append(TimeUtil.approxTimeUntil((int) ((offer.getTimestamp() - time) / 1000 + TIME_TO_EXPIRING)));
			builder.append(".");
			sendMessage(offer.getOfferer(), builder);
		}
		
		// timeStamp should be set here, rather than after all the checks, as it's used only 
		// for figuring out if the player has been warned before. Thus storing the time value
		// used here.
		timeStamp = time;
	}
}
