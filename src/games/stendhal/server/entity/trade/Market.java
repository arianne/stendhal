package games.stendhal.server.entity.trade;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class Market extends PassiveEntity {
	
	public static final String MARKET_RPCLASS_NAME = "market";
	public static final String EARNINGS_SLOT_NAME = "earnings";
	public static final String OFFERS_SLOT_NAME = "offers";
	public static final String EXPIRED_OFFERS_SLOT_NAME = "expired_offers";
	
	private final Set<Earning> earnings = new HashSet<Earning>();
	private final List<Offer> offers = new LinkedList<Offer>();
	private final List<Offer> expiredOffers = new LinkedList<Offer>();
	
	public static void generateRPClass() {
		final RPClass shop = new RPClass(MARKET_RPCLASS_NAME);
		shop.isA("entity");
		shop.addRPSlot(OFFERS_SLOT_NAME,-1);
		shop.addRPSlot(EARNINGS_SLOT_NAME, -1);
		shop.addRPSlot(EXPIRED_OFFERS_SLOT_NAME, -1);
	}
	
	public Market(final RPObject object) {
		super(object);
		this.setRPClass(MARKET_RPCLASS_NAME);
		if(!this.hasSlot(OFFERS_SLOT_NAME)) {
			addSlot(OFFERS_SLOT_NAME);
		}
		if(!this.hasSlot(EARNINGS_SLOT_NAME)) {
			addSlot(EARNINGS_SLOT_NAME);
		}
		if(!this.hasSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			addSlot(EXPIRED_OFFERS_SLOT_NAME);
		}
		if (object.hasSlot(OFFERS_SLOT_NAME)) {
			for(final RPObject rpo : object.getSlot(OFFERS_SLOT_NAME)) {
				this.offers.add((Offer) rpo);
				this.getSlot(OFFERS_SLOT_NAME).add(rpo);
			}
		}
		if (object.hasSlot(EARNINGS_SLOT_NAME)) {
			for(final RPObject rpo : object.getSlot(EARNINGS_SLOT_NAME)) {
				final Earning earning = (Earning) rpo;
				this.earnings.add(earning);
				this.getSlot(EARNINGS_SLOT_NAME).add(rpo);
			}
		}
		if (object.hasSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			for(final RPObject rpo : object.getSlot(EXPIRED_OFFERS_SLOT_NAME)) {
				this.expiredOffers.add((Offer) rpo);
				this.getSlot(EXPIRED_OFFERS_SLOT_NAME).add(rpo);
			}
		}
		store();
	}
	
	public static Market createShop() {
		Market shop = new Market();
		shop.store();
		return shop;
	}

	private Market() {
		super();
		setRPClass(MARKET_RPCLASS_NAME);
		if(!this.hasSlot(OFFERS_SLOT_NAME)) {
			addSlot(OFFERS_SLOT_NAME);
		}
		if(!this.hasSlot(EARNINGS_SLOT_NAME)) {
			addSlot(EARNINGS_SLOT_NAME);
		}
		if(!this.hasSlot(EARNINGS_SLOT_NAME)) {
			addSlot(EXPIRED_OFFERS_SLOT_NAME);
		}
		store();
	}
	
	/**
	 * creates a new offer at the market
	 * 
	 * @param offerer offering player
	 * @param item item to sell
	 * @param money price for the item
	 * @return the new created offer
	 */
	public Offer createOffer(final Player offerer, final Item item,
			final Integer money) {
		String name = offerer.getName();
		final Offer offer = new Offer(item, money, name);
		if(offerer.drop(item.getName())) {
			getOffers().add(offer);
			RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
			slot.add(offer);
			offerer.store();
			offer.store();
			this.store();
			return offer;
		}
		this.getZone().storeToDatabase();
		return offer;
	}

	/**
	 * Completes a trade of an offer by transfering item to accepting player and taking the money from him
	 * @param offer
	 * @param acceptingPlayer
	 */
	public void acceptOffer(final Offer offer, final Player acceptingPlayer) {
		if (getOffers().contains(offer)) {
			if (acceptingPlayer.drop("money", offer.getPrice().intValue())) {
				Item item = offer.getItem();
				acceptingPlayer.equipOrPutOnGround(item);
				final Earning earning = new Earning(item.getName(), offer.getPrice(), offer.getOfferer());
				this.getSlot(EARNINGS_SLOT_NAME).add(earning);
				offer.getSlot(Offer.OFFER_ITEM_SLOT_NAME).remove(item.getID());
				offers.remove(offer);
				this.getSlot(OFFERS_SLOT_NAME).remove(offer.getID());
				earning.store();
				this.store();
				Player sellingPlayer = SingletonRepository.getRuleProcessor().getPlayer(offer.getOfferer());
				applyTradingBonus(acceptingPlayer, sellingPlayer);
				acceptingPlayer.store();
				sellingPlayer.store();
			}
		}
		this.getZone().storeToDatabase();
	}

	private void applyTradingBonus(Player acceptingPlayer, Player sellingPlayer) {
		acceptingPlayer.incrementTradescore();
		sellingPlayer.incrementTradescore();
	}

	/**
	 * The earnings for complete trades are paid to the player
	 * 
	 * @param earner the player fetching his earnings
	 * @return the fetched earnings
	 */
	public Set<Earning> fetchEarnings(final Player earner) {
		Set<Earning> earningsToRemove = new HashSet<Earning>();
		for (RPObject earningRPObject : this.getSlot(EARNINGS_SLOT_NAME)) {
			Earning earning = (Earning) earningRPObject;
			if(earning.getSeller().equals(earner.getName())) {
				final StackableItem item = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
				item.setQuantity(this.sumUpEarningsForPlayer(earner));
				earner.equipToInventoryOnly(item);
				earnings.remove(earning);
				earningsToRemove.add(earning);
			}
		}
		for(Earning earning : earningsToRemove) {
			this.getSlot(EARNINGS_SLOT_NAME).remove(earning.getID());
		}
		this.getZone().storeToDatabase();
		return earningsToRemove;
	}
	
	/**
	 * counts the number of offers, a player has placed
	 * 
	 * @param offerer
	 * @return the number of offers
	 */
	public int countOffersOfPlayer(Player offerer) {
		int count = 0;
		for (Offer offer : this.offers) {
			if(offer.getOfferer().equals(offerer.getName())) {
				count = count + 1;
			}
		}
		return count;
	}

	/**
	 * removes an offer from the market and returns the item to the user
	 * @param o the offer to remove
	 * @param p the removing player
	 */
	public void removeOffer(Offer o, Player p) {
		p.equipOrPutOnGround(o.getItem());
		o.getSlot(Offer.OFFER_ITEM_SLOT_NAME).remove(o.getItem().getID());
		this.getOffers().remove(o);
		this.getSlot(OFFERS_SLOT_NAME).remove(o.getID());
		this.getZone().storeToDatabase();
	}
	
	public void expireOffer(Offer o) {
		this.getOffers().remove(o);
		this.getSlot(OFFERS_SLOT_NAME).remove(o.getID());
		this.expiredOffers.add(o);
		this.getSlot(EXPIRED_OFFERS_SLOT_NAME).add(o);
		this.getZone().storeToDatabase();
	}

	private int sumUpEarningsForPlayer(final Player earner) {
		int sum = 0;
		for (final RPObject earningRPObject : this.getSlot(EARNINGS_SLOT_NAME)) {
			Earning earning = (Earning) earningRPObject;
			if(earning.getSeller().equals(earner.getName())) {
				sum += earning .getValue().intValue();
			}
		}
		return sum;
	}

	public List<Offer> getOffers() {
		return offers;
	}
	
	public List<Offer> getExpiredOffers() {
		return expiredOffers;
	}

	public void removeExpiredOffer(Offer offerToRemove) {
		this.expiredOffers.remove(offerToRemove);
		this.getSlot(EXPIRED_OFFERS_SLOT_NAME).remove(offerToRemove.getID());
		this.getZone().storeToDatabase();
	}

	public Offer prolongOffer(Offer o) {
		if (this.expiredOffers.remove(o)) {
			this.getSlot(EXPIRED_OFFERS_SLOT_NAME).remove(o.getID());
		}
		if (this.offers.remove(o)) {
			this.getSlot(OFFERS_SLOT_NAME).remove(o.getID());
		}
		final Offer offer = new Offer(o.getItem(), o.getPrice(), o.getOfferer());
		getOffers().add(offer);
		RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
		slot.add(offer);
		offer.store();
		this.store();
		this.getZone().storeToDatabase();
		return offer;
	}

}
