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
package games.stendhal.server.entity.trade;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.LogSimpleItemEventCommand;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A Market handles sales offers of players. Players can place offers or accept
 * offers to buy a desired item. Offers last only for a certain amount of time
 * before they expire and can be prolonged. Expired Offers get removed
 * permanently from the market after another period of time. When an offer has
 * been accepted, the offering Player can come and fetch his earnings for that
 * sale.
 *
 * @author madmetzger, kiheru
 */
public class Market extends PassiveEntity {
	private static Logger logger = Logger.getLogger(Market.class);

	/**
	 * the RPClass name for the market
	 */
	public static final String MARKET_RPCLASS_NAME = "market";
	/**
	 * the name of the slot where the earnings are stored
	 */
	public static final String EARNINGS_SLOT_NAME = "earnings";
	/**
	 * the name of the slot where the offers are stored
	 */
	public static final String OFFERS_SLOT_NAME = "offers";
	/**
	 * the name of the slot where the expired offers are stored
	 */
	public static final String EXPIRED_OFFERS_SLOT_NAME = "expired_offers";

	/**
	 * Generate the RPClass for the Market
	 */
	public static void generateRPClass() {
		final RPClass shop = new RPClass(MARKET_RPCLASS_NAME);
		shop.isA("entity");
		shop.addRPSlot(OFFERS_SLOT_NAME, -1, Definition.HIDDEN);
		shop.addRPSlot(EARNINGS_SLOT_NAME, -1, Definition.HIDDEN);
		shop.addRPSlot(EXPIRED_OFFERS_SLOT_NAME, -1, Definition.HIDDEN);
	}

	/**
	 * Creates a new Market from an RPObject
	 *
	 * @param object
	 */
	public Market(final RPObject object) {
		super(object);
		this.setRPClass(MARKET_RPCLASS_NAME);
		hide();

		// delete the slots whose contents get wrong types
		// when loaded from the db
		if (hasSlot(OFFERS_SLOT_NAME)) {
			removeSlot(OFFERS_SLOT_NAME);
		}
		addSlot(OFFERS_SLOT_NAME);

		if (hasSlot(EARNINGS_SLOT_NAME)) {
			removeSlot(EARNINGS_SLOT_NAME);
		}
		addSlot(EARNINGS_SLOT_NAME);

		if (hasSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			removeSlot(EXPIRED_OFFERS_SLOT_NAME);
		}
		addSlot(EXPIRED_OFFERS_SLOT_NAME);

		// copy the contents from the old slots
		if (object.hasSlot(OFFERS_SLOT_NAME)) {
			for (final RPObject rpo : object.getSlot(OFFERS_SLOT_NAME)) {
				Offer offer = new Offer(rpo);

				// an offer might have become obsolete, when items are removed
				if (offer.getItem() == null) {
					logger.warn("Cannot restore an offer by "
							+ offer.getOfferer() + " because this item"
							+ " was removed from items.xml");
					continue;
				}

				this.getSlot(OFFERS_SLOT_NAME).add(offer);
			}
		}
		if (object.hasSlot(EARNINGS_SLOT_NAME)) {
			for (final RPObject rpo : object.getSlot(EARNINGS_SLOT_NAME)) {
				final Earning earning = new Earning(rpo);
				this.getSlot(EARNINGS_SLOT_NAME).add(earning);
			}
		}
		if (object.hasSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			for (final RPObject rpo : object.getSlot(EXPIRED_OFFERS_SLOT_NAME)) {
				Offer offer = new Offer(rpo);

				// an offer might have become obsolete, when items are removed
				if (offer.getItem() == null) {
					logger.warn("Cannot restore an offer by "
							+ offer.getOfferer() + " because this item"
							+ " was removed from items.xml");
					continue;
				}

				this.getSlot(EXPIRED_OFFERS_SLOT_NAME).add(offer);
			}
		}
		store();
	}

	/**
	 * Factory method for the market
	 *
	 * @return a new Market
	 */
	public static Market createShop() {
		Market shop = new Market();
		return shop;
	}

	private Market() {
		super();
		setRPClass(MARKET_RPCLASS_NAME);
		hide();
		if (!this.hasSlot(OFFERS_SLOT_NAME)) {
			addSlot(OFFERS_SLOT_NAME);
		}
		if (!this.hasSlot(EARNINGS_SLOT_NAME)) {
			addSlot(EARNINGS_SLOT_NAME);
		}
		if (!this.hasSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			addSlot(EXPIRED_OFFERS_SLOT_NAME);
		}
		store();
	}

	/**
	 * creates a new offer at the market
	 *
	 * @param offerer
	 *            offering player
	 * @param item
	 *            item to sell
	 * @param money
	 *            price for the item
	 * @param number
	 *            number of items to sell
	 * @return the new created offer
	 */
	public Offer createOffer(final Player offerer, Item item,
			final Integer money, final Integer number) {
		if (item == null || item.isBound()) {
			return null;
		}

		if (item instanceof StackableItem) {
			if (!offerer.equals(item.getBaseContainer())) {
				return null;
			}
			item = ((StackableItem) item).splitOff(number);
			if (item == null) {
				return null;
			}
		} else if (!offerer.drop(item)) {
			return null;
		}

		Offer offer = new Offer(item, money, offerer);
		RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
		slot.add(offer);
		getZone().storeToDatabase();

		new ItemLogger().addLogItemEventCommand(new LogSimpleItemEventCommand(
				item, offerer, "slot-to-market", item.get("name"), Integer
						.toString(getQuantity(item)), "new offer",
				OFFERS_SLOT_NAME));

		return offer;
	}

	/**
	 * Completes a trade of an offer by transferring item to accepting player
	 * and taking the money from him
	 *
	 * @param offer
	 * @param acceptingPlayer
	 * @return <code>true</code> if the trade was done, <code>false</code> on
	 * 	failure
	 */
	public boolean acceptOffer(final Offer offer, final Player acceptingPlayer) {
		if (getSlot(OFFERS_SLOT_NAME).has(offer.getID()) && offer.hasItem()) {
			int price = offer.getPrice().intValue();
			// Take the money; free items should always succeed
			if ((price == 0) || acceptingPlayer.drop("money", price)) {
				Item item = offer.getItem();
				offer.getSlot(Offer.OFFER_ITEM_SLOT_NAME).remove(item.getID());
				acceptingPlayer.equipOrPutOnGround(item);
				// Do not give trading bonus for accepting free items
				boolean reward = offer.shouldReward(acceptingPlayer)
						&& price > 0;
				final Earning earning = new Earning(offer.getPrice(),
						offer.getOfferer(), reward);
				this.getSlot(EARNINGS_SLOT_NAME).add(earning);
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
				new ItemLogger()
						.addLogItemEventCommand(new LogSimpleItemEventCommand(
								item, acceptingPlayer, "market-to-" + target,
								item.get("name"), Integer
										.toString(getQuantity(item)),
								"accept offer", slotName));

				this.getZone().storeToDatabase();
				return true;
			}
		}

		return false;
	}

	/**
	 * rewards player for a successfull trade
	 *
	 * @param player
	 *            the player to reward
	 */
	private void applyTradingBonus(Player player) {
		player.incrementTradescore();
	}

	/**
	 * The earnings for complete trades are paid to the player.
	 *
	 * @param earner
	 *            the player fetching his earnings
	 * @return the fetched earnings
	 */
	public Set<Earning> fetchEarnings(final Player earner) {
		Set<Earning> earningsToRemove = new HashSet<Earning>();
		for (RPObject earningRPObject : this.getSlot(EARNINGS_SLOT_NAME)) {
			Earning earning = (Earning) earningRPObject;
			if (earning.getSeller().equals(earner.getName())) {
				earningsToRemove.add(earning);
			}
		}

		if(!earningsToRemove.isEmpty()) {
			int summedUpEarnings = 0;
			//sum up
			for (Earning earningToSumUp : earningsToRemove) {
				summedUpEarnings = summedUpEarnings + earningToSumUp.getValue();
			}
			final StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
			item.setQuantity(summedUpEarnings);
			if(earner.equipToInventoryOnly(item)) {
				//reward only if equipping was done
				for(Earning earningToReward : earningsToRemove) {
					if (earningToReward.shouldReward()) {
						applyTradingBonus(earner);
					}
				}
				removeEarnings(earningsToRemove);
			} else {
				//signal that no earnings were collected at all
				//a caller can only distinguish if anything was collected
				earningsToRemove.clear();
			}
		}

		return earningsToRemove;
	}

	/**
	 * Remove a set of earnings.
	 *
	 * @param earningsToRemove
	 *            The earnings to be removed
	 */
	public void removeEarnings(Iterable<Earning> earningsToRemove) {
		for (Earning earning : earningsToRemove) {
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
		for (RPObject object : this.getSlot(OFFERS_SLOT_NAME)) {
			Offer offer = (Offer) object;
			if (offer.getOfferer().equals(offerer.getName())) {
				count = count + 1;
			}
		}
		return count;
	}

	/**
	 * removes an offer from the market and returns the item to the user
	 *
	 * @param o
	 *            the offer to remove
	 * @param p
	 *            the removing player
	 */
	public void removeOffer(Offer o, Player p) {
		Item item = o.getItem();
		String itemName = item.getName();

		o.getSlot(Offer.OFFER_ITEM_SLOT_NAME).remove(item.getID());
		p.equipOrPutOnGround(item);

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
		new ItemLogger().addLogItemEventCommand(new LogSimpleItemEventCommand(item, p,
						"market-to-" + target, itemName, Integer
								.toString(getQuantity(item)), "remove offer",
						slotName));
	}

	/**
	 * expires an offer and removes it from the available offers
	 *
	 * @param o
	 *            the offer to expire
	 */
	public void expireOffer(Offer o) {
		this.getSlot(OFFERS_SLOT_NAME).remove(o.getID());
		this.getSlot(EXPIRED_OFFERS_SLOT_NAME).add(o);
		this.getZone().storeToDatabase();
		String itemname = "null";
		if (o.hasItem()) {
			itemname = o.getItem().getName();
		}
		new GameEvent("market", "expire-offer", o.getOfferer(), itemname, o.getPrice().toString()).raise();
	}

	/**
	 * @return all currently expired offers in the market
	 */
	public List<Offer> getExpiredOffers() {
		List<Offer> expiredOffers = new LinkedList<Offer>();
		for(RPObject o : getSlot(EXPIRED_OFFERS_SLOT_NAME)) {
			expiredOffers.add((Offer) o);
		}
		return expiredOffers;
	}

	/**
	 * removes an expired offer permanently from the market
	 *
	 * @param offerToRemove
	 */
	public void removeExpiredOffer(Offer offerToRemove) {
		this.getSlot(EXPIRED_OFFERS_SLOT_NAME).remove(offerToRemove.getID());

		Item item = offerToRemove.getItem();
		if (item != null) {
			new ItemLogger().destroy(null, this.getSlot(EXPIRED_OFFERS_SLOT_NAME),
					item, "timeout");
		}

		this.getZone().storeToDatabase();
	}

	/**
	 * prolongs an offer in the market to make it available again
	 *
	 * @param offer
	 *            the offer to prolong
	 * @return the prolonged offer
	 */
	public Offer prolongOffer(Offer offer) {
		offer.updateTimestamp();
		if (getSlot(EXPIRED_OFFERS_SLOT_NAME).has(offer.getID())) {
			// It had expired. Move to active offers slot.
			this.getSlot(EXPIRED_OFFERS_SLOT_NAME).remove(offer.getID());
			RPSlot slot = this.getSlot(OFFERS_SLOT_NAME);
			slot.add(offer);
		} else if (!getSlot(OFFERS_SLOT_NAME).has(offer.getID())) {
			// Such an offer does not exist anymore
			return null;
		}

		this.getZone().storeToDatabase();
		return offer;
	}

	/**
	 * Get a list of offers whose timestamp is older than specified.
	 *
	 * @param seconds
	 *            age of offers in seconds
	 * @return list of offers that are older than the specified time
	 */
	public List<Offer> getOffersOlderThan(int seconds) {
		List<Offer> offers = new LinkedList<Offer>();
		for (RPObject o : getSlot(OFFERS_SLOT_NAME)) {
			offers.add((Offer) o);
		}
		return getOlderThan(offers, seconds);
	}

	/**
	 * Get a list of expired offers whose timestamp is older than specified.
	 *
	 * @param seconds
	 *            age of offers in seconds
	 * @return list of expired offers that are older than the specified time
	 */
	public List<Offer> getExpiredOffersOlderThan(int seconds) {
		return getOlderThan(getExpiredOffers(), seconds);
	}

	/**
	 * Get a list of earnings whose timestamp is older than specified.
	 *
	 * @param seconds
	 *            age of offers in seconds
	 * @return list of earnings that are older than the specified time
	 */
	public List<Earning> getEarningsOlderThan(int seconds) {
		RPSlot slot = this.getSlot(EARNINGS_SLOT_NAME);
		List<Earning> earnings = new LinkedList<Earning>();
		for (RPObject o : slot) {
			earnings.add((Earning) o);
		}
		return getOlderThan(earnings, seconds);
	}

	/**
	 * retrieves Dateable objects older than seconds from a given Iterable
	 *
	 * @param <T>
	 * @param set
	 *            the set to search in
	 * @param seconds
	 *            the maximum age
	 * @return the filtered list
	 */
	private <T extends Dateable> List<T> getOlderThan(Iterable<T> set,
			int seconds) {
		List<T> old = new LinkedList<T>();
		for (T obj : set) {
			if (System.currentTimeMillis() > obj.getTimestamp() + 1000L
					* seconds) {
				old.add(obj);
			}
		}

		return old;
	}

	/**
	 * gets the quantity of an item
	 *
	 * @param item
	 * @return the quantity
	 */
	private int getQuantity(Item item) {
		int quantity = 1;
		if (item instanceof StackableItem) {
			quantity = ((StackableItem) item).getQuantity();
		}

		return quantity;
	}

	/**
	 * @param o
	 * @return true iff the Offer o is in this market's offers
	 */
	public boolean contains(Offer o) {
		return getSlot(OFFERS_SLOT_NAME).has(o.getID());
	}

	/**
	 * @param player
	 * @return true iff there are earnings for this player in the market
	 */
	public boolean hasEarningsFor(Player player) {
		for(RPObject o : this.getSlot(EARNINGS_SLOT_NAME)) {
			Earning e = (Earning) o;
			if(e.getSeller().equals(player.getName())) {
				return true;
			}
		}
		return false;
	}
}
