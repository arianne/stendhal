package games.stendhal.server.entity.trade;

import games.stendhal.server.core.engine.ItemLogEntry;
import games.stendhal.server.core.engine.ItemLogger;
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

import org.apache.log4j.Logger;

public class Market extends PassiveEntity {
	private static Logger logger = Logger.getLogger(Market.class);
	
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
		hide();
		
		// delete the slots whose contents get wrong types
		// when loaded from the db
		if(hasSlot(OFFERS_SLOT_NAME)) {
			removeSlot(OFFERS_SLOT_NAME);
		} 
		addSlot(OFFERS_SLOT_NAME);
		
		if(hasSlot(EARNINGS_SLOT_NAME)) {
			removeSlot(EARNINGS_SLOT_NAME);
		}
		addSlot(EARNINGS_SLOT_NAME);
		
		if(hasSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			removeSlot(EXPIRED_OFFERS_SLOT_NAME);
		}
		addSlot(EXPIRED_OFFERS_SLOT_NAME);
		
		// copy the contents from the old slots
		if (object.hasSlot(OFFERS_SLOT_NAME)) {
			for(final RPObject rpo : object.getSlot(OFFERS_SLOT_NAME)) {
				Offer offer = new Offer(rpo);
				
				// an offer might have become obsolete, when items are removed
				if (offer.getItem() == null) {
					logger.warn("Cannot restore an offer by " + offer.getOfferer()
							+ " because this item"
							+ " was removed from items.xml");
					continue;
				}
				
				this.offers.add(offer);
				this.getSlot(OFFERS_SLOT_NAME).add(offer);
			}
		}
		if (object.hasSlot(EARNINGS_SLOT_NAME)) {
			for(final RPObject rpo : object.getSlot(EARNINGS_SLOT_NAME)) {
				final Earning earning = new Earning(rpo);
				this.earnings.add(earning);
				this.getSlot(EARNINGS_SLOT_NAME).add(earning);
			}
		}
		if (object.hasSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			for(final RPObject rpo : object.getSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			 	Offer offer = new Offer(rpo);
			 	
			 	// an offer might have become obsolete, when items are removed
				if (offer.getItem() == null) {
					logger.warn("Cannot restore an offer by " + offer.getOfferer()
							+ " because this item"
							+ " was removed from items.xml");
					continue;
				}
				
				this.expiredOffers.add(offer);
				this.getSlot(EXPIRED_OFFERS_SLOT_NAME).add(offer);
			}
		}
		store();
	}
	
	public static Market createShop() {
		Market shop = new Market();
		return shop;
	}

	private Market() {
		super();
		setRPClass(MARKET_RPCLASS_NAME);
		hide();
		if(!this.hasSlot(OFFERS_SLOT_NAME)) {
			addSlot(OFFERS_SLOT_NAME);
		}
		if(!this.hasSlot(EARNINGS_SLOT_NAME)) {
			addSlot(EARNINGS_SLOT_NAME);
		}
		if(!this.hasSlot(EXPIRED_OFFERS_SLOT_NAME)) {
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
	 * @param number number of items to sell
	 * @return the new created offer
	 */
	public Offer createOffer(final Player offerer, final Item item,
			final Integer money, final Integer number) {
		String name = offerer.getName();

		if (item == null || item.isBound()) {
			return null;
		}
	
		Offer offer = null;
		if(offerer.drop(item.getName())) {
			if(item instanceof StackableItem) {
				StackableItem itemStack = (StackableItem) item;
				StackableItem rest = itemStack.splitOff(item.getQuantity()-number);
				offerer.equipOrPutOnGround(rest);
			}
			offer = new Offer(item, money, offerer, number);
			getOffers().add(offer);
			RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
			slot.add(offer);
			getZone().storeToDatabase();
			
			new ItemLogger().addItemLogEntry(new ItemLogEntry(item, offerer, "slot-to-market", item.get("name"), Integer.toString(getQuantity(item)), "new offer", OFFERS_SLOT_NAME));
		}
		
		return offer;
	}

	/**
	 * Completes a trade of an offer by transferring item to accepting player and taking the money from him
	 * @param offer
	 * @param acceptingPlayer
	 */
	public boolean acceptOffer(final Offer offer, final Player acceptingPlayer) {
		if (getOffers().contains(offer)) {
			if (acceptingPlayer.drop("money", offer.getPrice().intValue())) {
				Item item = offer.getItem();
				offer.getSlot(Offer.OFFER_ITEM_SLOT_NAME).remove(item.getID());
				acceptingPlayer.equipOrPutOnGround(item);
				boolean reward = offer.shouldReward(acceptingPlayer); 
				final Earning earning = new Earning(offer.getPrice(), offer.getOfferer(), reward);
				this.getSlot(EARNINGS_SLOT_NAME).add(earning);
				earnings.add(earning);
				offers.remove(offer);
				this.getSlot(OFFERS_SLOT_NAME).remove(offer.getID());
				if (reward) {
					applyTradingBonus(acceptingPlayer);
				}
				
				// log the item movement
				String slotName = null;
				String target = "ground";
				if (item.getContainerSlot() != null) {
					slotName = item.getContainerSlot().getName();
					target = "slot";
				}			
				new ItemLogger().addItemLogEntry(new ItemLogEntry(item, acceptingPlayer, "market-to-" + target, item.get("name"), Integer.toString(getQuantity(item)), "accept offer", slotName));
				
				this.getZone().storeToDatabase();
				return true;
			}
		}
		
		return false;
	}

	private void applyTradingBonus(Player player) {
		player.incrementTradescore();
	}

	/**
	 * The earnings for complete trades are paid to the player.
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
				item.setQuantity(earning.getValue());
				earner.equipToInventoryOnly(item);
				earningsToRemove.add(earning);
				applyTradingBonus(earner);
			}
		}
		
		removeEarnings(earningsToRemove);
		
		return earningsToRemove;
	}
	
	/**
	 * Remove a set of earnings.
	 * 
	 * @param earningsToRemove The earnings to be removed
	 */
	public void removeEarnings(Iterable<Earning> earningsToRemove) {
		for(Earning earning : earningsToRemove) {
			earnings.remove(earning);
			this.getSlot(EARNINGS_SLOT_NAME).remove(earning.getID());
		}
		this.getZone().storeToDatabase();
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
		Item item = o.getItem();
		o.getSlot(Offer.OFFER_ITEM_SLOT_NAME).remove(item.getID());
		p.equipOrPutOnGround(item);
		
		getOffers().remove(o);
		getSlot(OFFERS_SLOT_NAME).remove(o.getID());
		
		getExpiredOffers().remove(o);
		getSlot(EXPIRED_OFFERS_SLOT_NAME).remove(o.getID());
		
		getZone().storeToDatabase();
		
		// log the item movement
		String slotName = null;
		String target = "ground";
		if (item.getContainerSlot() != null) {
			slotName = item.getContainerSlot().getName();
			target = "slot";
		}			
		new ItemLogger().addItemLogEntry(new ItemLogEntry(item, p, "market-to-" + target, item.get("name"), Integer.toString(getQuantity(item)), "remove offer", slotName));
	}
	
	public void expireOffer(Offer o) {
		this.getOffers().remove(o);
		this.getSlot(OFFERS_SLOT_NAME).remove(o.getID());
		this.expiredOffers.add(o);
		this.getSlot(EXPIRED_OFFERS_SLOT_NAME).add(o);
		this.getZone().storeToDatabase();
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
		
		Item item = offerToRemove.getItem();
		new ItemLogger().addItemLogEntry(new ItemLogEntry(item, 
				null, "destroy", item.get("name"), Integer.toString(getQuantity(item)), "timeout", "market"));
		
		this.getZone().storeToDatabase();
	}

	public Offer prolongOffer(Offer offer) {
		offer.updateTimestamp();
		if (this.expiredOffers.remove(offer)) {
			// It had expired. Move to active offers slot.  
			this.getSlot(EXPIRED_OFFERS_SLOT_NAME).remove(offer.getID());
			getOffers().add(offer);
			RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
			slot.add(offer);
		} else if (!this.offers.contains(offer)) {
			// Such an offer does not exist anymore
			return null;
		}
		if (this.offers.remove(offer)) {
			this.getSlot(OFFERS_SLOT_NAME).remove(offer.getID());
		}
		Player offererPlayer = SingletonRepository.getRuleProcessor().getPlayer(offer.getOfferer());
		final Offer o = new Offer(offer.getItem(), offer.getPrice(), offererPlayer, offer.getNumber());
		getOffers().add(o);
		RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
		slot.add(offer);
		this.getZone().storeToDatabase();
		return offer;
	}

	/**
	 * Get a list of offers whose timestamp is older than specified.
	 * 
	 * @param seconds age of offers in seconds
	 * @return list of offers that are older than the specified time
	 */
	public List<Offer> getOffersOlderThan(int seconds) {
		return getOlderThan(offers, seconds);
	}
	
	/**
	 * Get a list of expired offers whose timestamp is older than specified.
	 * 
	 * @param seconds age of offers in seconds
	 * @return list of expired offers that are older than the specified time
	 */
	public List<Offer> getExpiredOffersOlderThan(int seconds) {
		return getOlderThan(expiredOffers, seconds);
	}
	
	/**
	 * Get a list of earnings whose timestamp is older than specified.
	 * 
	 * @param seconds age of offers in seconds
	 * @return list of earnings that are older than the specified time
	 */
	public List<Earning> getEarningsOlderThan(int seconds) {
		return getOlderThan(earnings, seconds);
	}
	
	private <T extends Dateable> List<T> getOlderThan(Iterable<T> set, int seconds) {
		List<T> old = new LinkedList<T>();
		for (T obj : set) {
			if (System.currentTimeMillis() > obj.getTimestamp() + 1000L * seconds) {
				old.add(obj);
			}
		}
		
		return old;
	}
	
	private int getQuantity(Item item) {
		int quantity = 1;
		if (item instanceof StackableItem) {
			quantity = ((StackableItem) item).getQuantity();
		}
		
		return quantity;
	}
}
