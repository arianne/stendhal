package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.trade.Offer;

public abstract class OfferHandler {
	private Offer offer;
	
	public abstract void add(SpeakerNPC npc);
	
	protected void setOffer(Offer offer) {
		this.offer = offer;
	}
	
	protected Offer getOffer() {
		return offer;
	}
	
	protected int getQuantity(Item item) {
		int quantity = 1;
		if (item instanceof StackableItem) {
			quantity = ((StackableItem) item).getQuantity();
		}
		
		return quantity;
	}
}
