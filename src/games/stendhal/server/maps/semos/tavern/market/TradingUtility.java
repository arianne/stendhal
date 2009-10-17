package games.stendhal.server.maps.semos.tavern.market;

import java.math.BigDecimal;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
/**
 * helper class for handling adding and prolonging offers
 * @author madmetzger
 */
public class TradingUtility {
	
	private static final int FEE_BONUS_CONSTANT = 10;
	private static final double TRADING_FEE_PERCENTAGE = 0.01;
	private static final double TRADING_FEE_PLAYER_KILLER_PENALTY = 0.5;
	static final int MAX_NUMBER_OFF_OFFERS = 3;
	/**
	 * number of days after which an offer will expire after warning
	 */
	static final int DAYS_TO_OFFER_EXPIRING_AFTER_WARNING = 3;
	/**
	 * number of days after which an offerer gets the expire warning
	 */
	private static final int DAYS_TO_OFFER_EXPIRE_WARNING_DELAY = 4;
	/**
	 * number of days after which an expired offer will be removed completely
	 */
	static final int DAYS_TO_OFFER_GETTING_REMOVED_COMPLETELY = 7;

	public static boolean substractTradingFee(Player player, int price) {
		BigDecimal fee = calculateFee(player, price);
		return player.drop("money", fee.intValue());
	}
	
	public static BigDecimal calculateFee(Player p, int price) {
		BigDecimal fee  = BigDecimal.valueOf(price);
		fee = fee.multiply(BigDecimal.valueOf(TRADING_FEE_PERCENTAGE));
		if(p.isBadBoy()) {
			fee = fee.multiply(BigDecimal.valueOf(TRADING_FEE_PLAYER_KILLER_PENALTY));
		}
		BigDecimal feeBonus = BigDecimal.ONE;
		
		feeBonus = BigDecimal.valueOf(Math.exp(p.getTradescore()/FEE_BONUS_CONSTANT));
		fee = fee.multiply(feeBonus);
		return fee.max(BigDecimal.ONE);
	}

	public static boolean isPlayerWithinOfferLimit(Player player) {
		return countOffers(player)< MAX_NUMBER_OFF_OFFERS;
	}
	
	private static int countOffers(Player player) {
		Market shopFromZone = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		if(shopFromZone != null) {
			int numberOfOffers = shopFromZone.countOffersOfPlayer(player);
			return numberOfOffers;
		}
		return 0;
	}
	
	public static void addTurnNotifiers(Player player, Offer o) {
		TurnListener offerExpireWarner = new OfferExpireWarner(o, player.getZone());
		TurnNotifier.get().notifyInTurns((DAYS_TO_OFFER_EXPIRE_WARNING_DELAY) * MathHelper.SECONDS_IN_ONE_DAY, offerExpireWarner);
		TurnListener offerExpirer = new OfferExpirerer(o,player.getZone());
		TurnNotifier.get().notifyInTurns(DAYS_TO_OFFER_EXPIRING_AFTER_WARNING + DAYS_TO_OFFER_EXPIRE_WARNING_DELAY * MathHelper.SECONDS_IN_ONE_DAY, offerExpirer);
		TurnListener offerRemover = new OfferRemover(o,player.getZone());
		TurnNotifier.get().notifyInTurns(DAYS_TO_OFFER_EXPIRING_AFTER_WARNING + DAYS_TO_OFFER_EXPIRE_WARNING_DELAY + DAYS_TO_OFFER_GETTING_REMOVED_COMPLETELY * MathHelper.SECONDS_IN_ONE_DAY, offerRemover);
	}

}
