package games.stendhal.server.maps.quests.houses;

import org.apache.log4j.Logger;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * House tax, and confiscation of houses
 */
public class HouseTax implements TurnListener {
	private static final Logger logger = Logger.getLogger(HouseTax.class);
	/* TODO: use reasonable times. 
	 * For now using ridiculously short times to test the expiring code */
	/** How often the tax should be paid. Time in seconds. */ 
	private static final int TAX_PAYMENT_PERIOD = 60 * 60; // * 24 * 30;
	/** How often the payments should be checked. Time in seconds */
	private static final int TAX_CHECKING_PERIOD = TAX_PAYMENT_PERIOD / 30;
	/** How many tax payments can be unpaid. Any more and the house will be confiscated */
	private static final int MAX_UNPAID_TAXES = 5;
	
	/** The base amount of tax per month */
	private static final int BASE_TAX = 1000;
	/** Interest rate for unpaid taxes. The taxman does not give free loans. */
	private static final double INTEREST_RATE = 0.1;
	
	
	private long previouslyChecked = 0;

	public HouseTax() {
		setupTaxman();
		SingletonRepository.getTurnNotifier().notifyInSeconds(TAX_CHECKING_PERIOD, this);
	}
	
	/**
	 * Get the amount of money a player owes to the state.
	 * 
	 * @param periods the number of months the player has to pay at once
	 * @return the amount of debt
	 */
	private int getTaxDebt(int periods) {
		int debt = 0; 
		
		for (int i = 0; i < periods; i++) {
			debt += BASE_TAX * Math.pow(1 + INTEREST_RATE, 0);
		}
		
		return debt;
	}
	
	/**
	 * Get the number of tax periods the player has neglegted to pay.
	 * 
	 * @param player the player to be checked
	 * @return number of periods
	 */
	private int getUnpaidTaxPeriods(final Player player) {
		HousePortal portal = HouseUtilities.getPlayersHouse(player);
		int payments = 0;

		if (portal != null) {
			final int timeDiffSeconds = (int) ((System.currentTimeMillis() - portal.getExpireTime()) / 1000);
			payments = Math.max(0, timeDiffSeconds / TAX_PAYMENT_PERIOD);
		}

		return payments;
	}
	
	private void setTaxesPaid(Player player, int periods) {
		HousePortal portal = HouseUtilities.getPlayersHouse(player);
		portal.setExpireTime(portal.getExpireTime() + periods * TAX_PAYMENT_PERIOD * 1000);
	}

	public void onTurnReached(int turn) {
		SingletonRepository.getTurnNotifier().notifyInSeconds(TAX_CHECKING_PERIOD, this);

		final long time =  System.currentTimeMillis();

		/*
		 * Decide the time window for notifying the players
		 * We can not rely on the time to be TAX_CHEKING_PERIOD, since
		 * there could have been overflows
		 */
		final long timeSinceChecked;
		if (previouslyChecked != 0) {
			timeSinceChecked = (time - previouslyChecked);
		} else {
			/*
			 * The server has been restarted since the last check, so the exact 
			 * run time is not known. Use a longer time window for notifications
			 * than normally.
			 * That results in duplicated notifications to some players, but
			 * that's better than not notifying some at all.
			 */
			timeSinceChecked = 2 * TAX_CHECKING_PERIOD;
		}
		previouslyChecked = time;


		// Check all houses, and decide if they need any action
		for (HousePortal portal : HouseUtilities.getHousePortals()) {
			String owner = portal.getOwner();
			if (owner.length() > 0) {		
				final int timeDiffSeconds = (int) ((time - portal.getExpireTime()) / 1000);
				final int payments = (int) timeDiffSeconds / TAX_PAYMENT_PERIOD;

				if (payments > MAX_UNPAID_TAXES) {
					confiscate(portal);
				} else if ((payments > 0) && ((timeDiffSeconds % TAX_PAYMENT_PERIOD) < (timeSinceChecked / 1000))) {
					// a new tax payment period has passed since the last check. notify the player
					notifyIfNeeded(owner, "You have not paid your house tax for " 
							+ Grammar.quantityplnoun(payments, "month") 
							+ ". Come to Ados townhall to pay your debts to the state.");
				}
			}
		}
	}

	/**
	 * Confiscate a house, and notify the owner.
	 * 
	 * @param portal the door of the house to confiscate
	 */
	private void confiscate(HousePortal portal) {
		notifyIfNeeded(portal.getOwner(), "You have negleted your house taxes too long. The house has been confiscated to cover the your debts to the crown.");
		logger.info("confiscated " + portal.getDoorId() + ", which used to belong to " + portal.getOwner());
		portal.changeLock();
		portal.setOwner("");
	}

	/**
	 * Notify the owner of a house in the name of Tax Man
	 * 
	 * @param owner the player to be notified
	 * @param message the delivered message
	 */
	private void notifyIfNeeded(String owner, String message) {
		// TODO: remove the support for "an unknown owner", once all the old
		// houses have been updated or confiscated
		if (!owner.equals("an unknown owner")) {
			logger.info("sending a notice to '" + owner + "': " + message);
			final Player postman = SingletonRepository.getRuleProcessor().getPlayer("postman");

			if (postman != null) {
				postman.sendPrivateText("Taxman tells you: tell " + owner + " " + message);
			} else {
				logger.warn("could not use postman to deliver the message");
			}
		}
	}
	
	private void setupTaxman() {
		SpeakerNPC taxman = SingletonRepository.getNPCList().get("Mr Taxman");
		
		taxman.addReply("tax", "All house owners must #pay taxes to the state.");
		
		taxman.add(ConversationStates.ATTENDING,
				"pay",
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						return getUnpaidTaxPeriods(player) > 0;
					}
		},
		ConversationStates.QUESTION_1,
		"Do you want to pay your taxes now?", 
		null);
		
		taxman.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						int periods = getUnpaidTaxPeriods(player);
						int cost = getTaxDebt(periods);
						if (player.isEquipped("money", cost)) {
							player.drop("money", cost);
							setTaxesPaid(player, periods);
							npc.say("Thank you! You have paid your taxes for last " 
									+ Grammar.quantityplnoun(periods, "month")
									+ ".");
						} else {
							npc.say("You don't have enough money to pay your taxes. You need at least" 
									+ cost + ".");
						}
				}
			});
	}
}
