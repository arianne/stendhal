/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.tavern.market;

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.trade.Earning;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
import games.stendhal.server.util.TimeUtil;
import marauroa.server.db.command.DBCommandQueue;

public class OfferExpirer implements TurnListener{
	private static Logger logger = Logger.getLogger(OfferExpirer.class);
	/**
	 * number of days after which an offer will expire after warning.
	 */
	private static final int DAYS_TO_EXPIRING = 3;
	/**
	 * number of days after which an offerer gets the expire warning.
	 */
	private static final int DAYS_TO_WARNING = 4;
	/**
	 * number of days after which an expired offer will be removed completely.
	 */
	private static final int DAYS_TO_REMOVING = 28;

	/**
	 * Time after earnings that the owner has not collected are pocketted by Harold.
	 */
	private static final int DAYS_BEFORE_REMOVING_EARNINGS = 28;

	/**
	 * Total time in seconds before sending the player a warning.
	 */
	private static final int TIME_TO_WARNING = DAYS_TO_WARNING * MathHelper.SECONDS_IN_ONE_DAY;
	/**
	 * Total time in seconds before expiring an offer.
	 */
	protected static final int TIME_TO_EXPIRING = (DAYS_TO_WARNING + DAYS_TO_EXPIRING) * MathHelper.SECONDS_IN_ONE_DAY;
	/**
	 * Total time in seconds before removing an offer completely.
	 */
	private static final int TIME_TO_REMOVING = (DAYS_TO_WARNING + DAYS_TO_EXPIRING + DAYS_TO_REMOVING) * MathHelper.SECONDS_IN_ONE_DAY;
	private long timeStamp = 0;

	private static final int TIME_TO_REMOVING_EARNINGS = DAYS_BEFORE_REMOVING_EARNINGS * MathHelper.SECONDS_IN_ONE_DAY;

	/**
	 * Time between checks in seconds.
	 */
	private static final int CHECKING_INTERVAL = MathHelper.SECONDS_IN_ONE_HOUR;
	// MathHelper.SECONDS_IN_ONE_MINUTE;

	private Market market;

	public OfferExpirer(Market market) {
		this.market = market;

		TurnNotifier.get().notifyInSeconds(CHECKING_INTERVAL, this);
	}

	@Override
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
		checkRemovedEarnings();

		TurnNotifier.get().notifyInSeconds(CHECKING_INTERVAL, this);
	}

	/**
	 * sends a message to player via postman
	 *
	 * @param player the recipient of the message
	 * @param message the message itself
	 */
	private void sendMessage(String player, StringBuilder message) {

		logger.debug("sending a notice to '" + player + "': " + message.toString());

		// there is an npc action to send the message but this is all we want to do here.
		DBCommandQueue.get().enqueue(new StoreMessageCommand("Harold", player, message.toString(), "N"));
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
			String itemname = "unknown item";
			if (offer.hasItem()) {
				itemname = offer.getItem().getName();
			}
			builder.append(Grammar.a_noun(itemname));
			builder.append(" has expired. You have ");
			builder.append(TimeUtil.approxTimeUntil((int) (offer.getTimestamp()
					- System.currentTimeMillis()) / 1000 + TIME_TO_REMOVING));
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
			builder.append(Grammar.a_noun(offer.getItemName()));
			builder.append(" has been removed permanently from the market.");
			sendMessage(offer.getOfferer(), builder);
		}
	}

	/**
	 * checks for offers that need a warning before expiring
	 */
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
			builder.append(Grammar.a_noun(offer.getItemName()));
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

	/**
	 * checks for earnings to remove and removes them if too old
	 */
	private void checkRemovedEarnings() {
		List<Earning> list = market.getEarningsOlderThan(TIME_TO_REMOVING_EARNINGS);
		for (Earning earning : list) {
			StringBuilder builder = new StringBuilder();
			builder.append("Your earning of ");
			builder.append(earning.getValue());
			builder.append(" money has expired and can no longer be collected.");
			sendMessage(earning.getSeller(), builder);
		}

		market.removeEarnings(list);
	}
}
