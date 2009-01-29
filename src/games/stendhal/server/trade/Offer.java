package games.stendhal.server.trade;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;

public class Offer {

	private final Item item;
	
	private final Integer price;

	private final String offererName;
	
	/**
	 * @param item
	 */
	public Offer(Item item, Integer price, String offererName) {
		super();
		this.item = item;
		this.price = price;
		this.offererName = offererName;
	}


	public final Item getItem() {
		return item;
	}



	public final Integer getPrice() {
		return price;
	}



	public final String getOffererName() {
		return offererName;
	}

	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				Offer.class);
	}
	
}
