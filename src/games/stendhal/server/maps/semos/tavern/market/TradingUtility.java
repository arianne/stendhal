package games.stendhal.server.maps.semos.tavern.market;

import java.math.BigDecimal;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Money;
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

	/**
	 * substracts the trading fee from the player depending on the given price
	 * 
	 * @param player
	 * @param price
	 * @return true iff player dropped the amount of money
	 */
	public static boolean substractTradingFee(Player player, int price) {
		BigDecimal fee = calculateFee(player, price);
		return player.drop("money", fee.intValue());
	}
	
	/**
	 * checks if a player can afford the trading fee depending on price
	 * 
	 * @param player
	 * @param price
	 * @return true iff player has enough money
	 */
	public static boolean canPlayerAffordTradingFee(Player player, int price) {
		BigDecimal fee = calculateFee(player, price);
		List<Item> allEquipped = player.getAllEquipped("money");
		int ownedMoney = 0;
		for(Item item : allEquipped) {
			Money m = (Money) item;
			ownedMoney += m.getQuantity();
		}
		return fee.compareTo(BigDecimal.valueOf(ownedMoney)) <= 0;
	}
	
	/**
	 * calculates the trading fee a player has to pay when selling for a certain price
	 * @param player
	 * @param price
	 * @return the trading fee
	 */
	public static BigDecimal calculateFee(Player player, int price) {
		BigDecimal fee  = BigDecimal.valueOf(price);
		fee = fee.multiply(BigDecimal.valueOf(TRADING_FEE_PERCENTAGE));
		if(player.isBadBoy()) {
			fee = fee.multiply(BigDecimal.valueOf(TRADING_FEE_PLAYER_KILLER_PENALTY));
		}
		BigDecimal feeBonus = BigDecimal.ONE;
		
		feeBonus = BigDecimal.valueOf(Math.exp(player.getTradescore()/FEE_BONUS_CONSTANT));
		fee = fee.multiply(feeBonus);
		return fee.max(BigDecimal.ONE);
	}

	/**
	 * checks if a player has not already placed the max number of offers
	 * 
	 * @param player
	 * @return
	 */
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
	
	/**
	 * adds the turn notifiers for handling expiring offers
	 * 
	 * @param player
	 * @param offer
	 */
	public static void addTurnNotifiers(Player player, Offer offer) {
		TurnListener offerExpireWarner = new OfferExpireWarner(offer, player.getZone());
		TurnNotifier.get().notifyInSeconds(DAYS_TO_OFFER_EXPIRE_WARNING_DELAY * MathHelper.SECONDS_IN_ONE_DAY, offerExpireWarner);
		TurnListener offerExpirer = new OfferExpirerer(offer,player.getZone());
		TurnNotifier.get().notifyInSeconds((DAYS_TO_OFFER_EXPIRING_AFTER_WARNING + DAYS_TO_OFFER_EXPIRE_WARNING_DELAY) * MathHelper.SECONDS_IN_ONE_DAY, offerExpirer);
		TurnListener offerRemover = new OfferRemover(offer,player.getZone());
		TurnNotifier.get().notifyInSeconds((DAYS_TO_OFFER_EXPIRING_AFTER_WARNING + DAYS_TO_OFFER_EXPIRE_WARNING_DELAY + DAYS_TO_OFFER_GETTING_REMOVED_COMPLETELY) * MathHelper.SECONDS_IN_ONE_DAY, offerRemover);
	}

}
