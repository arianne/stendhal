package games.stendhal.server.trade;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalPlayerDatabase;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;
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

public class Market extends RPEntity {
	
	public static final String MARKET_RPCLASS_NAME = "market";
	public static final String EARNINGS_SLOT_NAME = "earnings";
	public static final String OFFERS_SLOT_NAME = "offers";
	
	private final Set<Earning> earnings = new HashSet<Earning>();
	private final List<Offer> offers = new LinkedList<Offer>();
	
	public static void generateRPClass() {
		final RPClass shop = new RPClass(MARKET_RPCLASS_NAME);
		shop.isA("entity");
		shop.addRPSlot(OFFERS_SLOT_NAME,-1);
		shop.addRPSlot(EARNINGS_SLOT_NAME, -1);
	}
	
	public Market(final RPObject object) {
		super(object);
		this.setRPClass(MARKET_RPCLASS_NAME);
		addSlot(new RPSlot(OFFERS_SLOT_NAME));
		addSlot(new RPSlot(EARNINGS_SLOT_NAME));
		for(final RPObject rpo : object.getSlot(OFFERS_SLOT_NAME)) {
			this.offers.add((Offer) rpo);
			this.getSlot(OFFERS_SLOT_NAME).add(rpo);
		}
		for(final RPObject rpo : object.getSlot(EARNINGS_SLOT_NAME)) {
			final Earning earning = (Earning) rpo;
			this.earnings.add(earning);
			this.getSlot(EARNINGS_SLOT_NAME).add(rpo);
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
		addSlot(new RPSlot(OFFERS_SLOT_NAME));
		addSlot(new RPSlot(EARNINGS_SLOT_NAME));
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
		final Offer offer = new Offer(item, money, offerer.getName());
		if(offerer.drop(item.getName())) {
			getOffers().add(offer);
			RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
			slot.add(offer);
			offerer.store();
			offer.store();
			this.store();
			return offer;
		}
		return null;
	}

	/**
	 * Completes a trade of an offer by transfering item to accepting player and taking the money from him
	 * @param offer
	 * @param acceptingPlayer
	 */
	public void acceptOffer(final Offer offer, final Player acceptingPlayer) {
		if (getOffers().contains(offer)) {
			if (acceptingPlayer.drop("money", offer.getPrice().intValue())) {
				acceptingPlayer.equipOrPutOnGround(offer.getItem());
				final Earning earning = new Earning(offer.getItem(), offer.getPrice(), offer.getOffererName());
				this.getSlot(EARNINGS_SLOT_NAME).add(earning);
				offer.getSlot(Offer.OFFER_ITEM_SLOT_NAME).remove(offer.getItem().getID());
				offers.remove(offer);
				this.getSlot(OFFERS_SLOT_NAME).remove(offer.getID());
				earning.store();
				this.store();
				Player sellingPlayer = null;
				applyTradingBonus(acceptingPlayer, sellingPlayer);
			}
		}
	}

	private void applyTradingBonus(Player acceptingPlayer, Player sellingPlayer) {
		// TODO Auto-generated method stub
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
			if(offer.getOffererName().equals(offerer.getName())) {
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

	@Override
	protected void dropItemsOn(Corpse corpse) {
		// 
		
	}

	@Override
	public void logic() {
		//
	}

}
