package games.stendhal.server.entity.trade;

import java.util.Map;

import games.stendhal.server.actions.CIDSubmitAction;
import games.stendhal.server.core.engine.transformer.ItemTransformer;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

public class Offer extends Entity implements Dateable {
	private static final Logger logger = Logger.getLogger(Offer.class);

	private static final String OFFERER_ATTRIBUTE_NAME = "offerer";
	private static final String OFFERER_CID_ATTRIBUTE = "offerer_cid";
	private static final String PRICE_ATTRIBUTE = "price";

	public static final String OFFER_ITEM_SLOT_NAME = "item";

	public static final String OFFER_RPCLASS_NAME = "offer";
	private static final String TIMESTAMP = "timestamp";

	private Item item;
	private final Integer price;
	private final String offerer;

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
	 * @param item
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
			this.item = item;
		}
		this.put(PRICE_ATTRIBUTE, price.intValue());
		this.price = price;
		this.put(OFFERER_ATTRIBUTE_NAME, offerer.getName());
		this.offerer = offerer.getName();
		put(OFFERER_CID_ATTRIBUTE, getPlayerCID(offerer));
		updateTimestamp();
	}

	public Offer(final RPObject object) {
		super(object);
		setRPClass("offer");
		hide();
		
		price = getInt(PRICE_ATTRIBUTE);
		offerer = get(OFFERER_ATTRIBUTE_NAME);
		
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

		item = entity;
		getSlot(OFFER_ITEM_SLOT_NAME).addPreservingId(item);
	}

	public final Item getItem() {
		return item;
	}

	public final Integer getPrice() {
		return price;
	}

	public final String getOfferer() {
		return offerer;
	}

	/**
	 * Get the creation or renewal time of the offer.
	 * 
	 * @return Timestamp in milliseconds
	 */
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

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false, Offer.class);
	}

	private String getPlayerCID(Player player) {
		Map<String, String> nameList = CIDSubmitAction.nameList;
		String cid = nameList.get(player.getName());
		if (cid == null) {
			return "";
		} else {
			return cid;
		}
	}
}
