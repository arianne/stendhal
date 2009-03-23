package games.stendhal.common;

public interface Constants {
	
	String ACCEPT_OFFER_TYPE = "ACCEPT_OFFER";
	
	String ACCEPT_OFFER_ITEM = "item";
	
	String ACCEPT_OFFER_PRICE = "price";
	
	String OFFER_GOODS = "goods";
	
	String ACTION_TYPE = "type";

	/**
	 * All the slots considered to be "with" the entity. Listed in priority
	 * order (i.e. bag first).
	 */
	String[] CARRYING_SLOTS = { "bag", "head", "rhand",
			"lhand", "armor", "finger", "cloak", "legs", "feet", "keyring" };

	String ACCEPT_OFFER_OFFERERNAME = "offererName";

}
