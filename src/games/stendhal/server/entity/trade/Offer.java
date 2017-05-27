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

import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.actions.CStatusAction;
import games.stendhal.server.core.engine.transformer.ItemTransformer;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * Represents an Offer for sale in the {@link Market}
 *
 * @author madmetzger
 */
public class Offer extends Entity implements Dateable {
	private static final Logger logger = Logger.getLogger(Offer.class);

	private static final String OFFERER_ATTRIBUTE_NAME = "offerer";
	private static final String OFFERER_CID_ATTRIBUTE = "offerer_cid";
	private static final String PRICE_ATTRIBUTE = "price";

	/**
	 * The name of the slot where the item for sale is stored
	 */
	public static final String OFFER_ITEM_SLOT_NAME = "item";

	/**
	 * The RPClass name of an Offer
	 */
	public static final String OFFER_RPCLASS_NAME = "offer";
	private static final String TIMESTAMP = "timestamp";

	public static void generateRPClass() {
		final RPClass offerRPClass = new RPClass(OFFER_RPCLASS_NAME);
		offerRPClass.isA("entity");
		offerRPClass.addAttribute(PRICE_ATTRIBUTE, Type.INT);
		offerRPClass.addAttribute(OFFERER_ATTRIBUTE_NAME, Type.STRING);
		offerRPClass.addAttribute(OFFERER_CID_ATTRIBUTE, Type.STRING);
		offerRPClass.addAttribute(TIMESTAMP, Type.STRING);
		offerRPClass.addRPSlot(OFFER_ITEM_SLOT_NAME, 1);
	}

	/**
	 * Create a new Offer.
	 *
	 * @param item offered item
	 * @param price price of the item
	 * @param offerer player making the offer
	 */
	public Offer(final Item item, final Integer price, final Player offerer) {
		super();
		setRPClass("offer");
		hide();
		if (!hasSlot(OFFER_ITEM_SLOT_NAME)) {
			this.addSlot(OFFER_ITEM_SLOT_NAME);
		}
		if (item != null) {
			getSlot(OFFER_ITEM_SLOT_NAME).add(item);
		}
		this.put(PRICE_ATTRIBUTE, price.intValue());
		this.put(OFFERER_ATTRIBUTE_NAME, offerer.getName());
		put(OFFERER_CID_ATTRIBUTE, getPlayerCID(offerer));
		updateTimestamp();
	}

	/**
	 * Creates an Offer from a RPObject
	 * @param object
	 */
	public Offer(final RPObject object) {
		super(object);
		setRPClass("offer");
		hide();

		getSlot(OFFER_ITEM_SLOT_NAME).clear();

		final RPObject itemObject = object.getSlot(OFFER_ITEM_SLOT_NAME).getFirst();

		final Item entity = new ItemTransformer().transform(itemObject);

		// log removed items
		if (entity == null) {
			int quantity = 1;
			if (itemObject.has("quantity")) {
				quantity = itemObject.getInt("quantity");
			}
			logger.warn("Cannot restore " + quantity + " "
					+ itemObject.get("name") + " to offer "
					+ " because this item was removed from items.xml");
			return;
		}

		getSlot(OFFER_ITEM_SLOT_NAME).addPreservingId(entity);
	}

	/**
	 * @return the Item for sale
	 */
	public final Item getItem() {
		if (getSlot(OFFER_ITEM_SLOT_NAME).size() == 0) {
			return null;
		}
		return (Item) getSlot(OFFER_ITEM_SLOT_NAME).iterator().next();
	}

	/**
	 * checks if an item is attached to this offer.
	 * @return true, if this offer has an item
	 */
	public boolean hasItem() {
		return (hasSlot(OFFER_ITEM_SLOT_NAME) && getSlot(OFFER_ITEM_SLOT_NAME).size() != 0);
	}

	/**
	 * gets the name of the item
	 *
	 * @return name of item or <code>"null"</code>, if there is no item in this offer
	 */
	public String getItemName() {
		if (hasItem()) {
			return getItem().getName();
		}
		logger.error("Trying to get item name from empty slot", new Throwable());
		return "null";
	}

	/**
	 * @return the price to pay for this offer when accepting it
	 */
	public final Integer getPrice() {
		return getInt(PRICE_ATTRIBUTE);
	}

	/**
	 * @return the name of the offering player
	 */
	public final String getOfferer() {
		return get(OFFERER_ATTRIBUTE_NAME);
	}

	/**
	 * Get the creation or renewal time of the offer.
	 *
	 * @return Timestamp in milliseconds
	 */
	@Override
	public long getTimestamp() {
		long timeStamp = 0;
		try {
			timeStamp = Long.parseLong(get(TIMESTAMP));
		} catch (final NumberFormatException e) {
			logger.error("Invalid timestamp: " + get(TIMESTAMP), e);
		}
		return timeStamp;
	}

	/**
	 * Update the timestamp of the offer to the current moment.
	 */
	public void updateTimestamp() {
		put(TIMESTAMP, Long.toString(System.currentTimeMillis()));
	}

	/**
	 * Check whether accepting this offer should be rewarder in trade score.
	 *
	 * @param player The player accepting the offer
	 * @return True iff the accepting the offer should be rewarded
	 */
	public boolean shouldReward(Player player) {
		String cid = getPlayerCID(player);

		// Do not reward if either the buyer or the offerer
		// does not have a proper CID for some reason
		if (cid.equals("") || "".equals(get(OFFERER_CID_ATTRIBUTE))
				|| cid.equals(get(OFFERER_CID_ATTRIBUTE))) {
			return false;
		}

		// Finally check if it's the same player from another computer
		return !player.getName().equals(getOfferer());
	}

	private String getPlayerCID(Player player) {
		Map<String, String> nameList = CStatusAction.nameList;
		String cid = nameList.get(player.getName());
		if (cid == null) {
			return "";
		} else {
			return cid;
		}
	}

}
