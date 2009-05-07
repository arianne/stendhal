package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.ObjectChangeListener;

import java.awt.Component;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

class OrderPanelController implements ObjectChangeListener {

	private Component component;
	
	private final RPObject object;

    protected OrderPanelController(final RPObject object) {
    	this.object = object;
		initialize(this.object);
	}

	private boolean initialize(final RPObject object) {
		String itemname = null;
		if (object.hasSlot("goods")) {
			final RPSlot slot = object.getSlot("goods");
			if (slot.getFirst() != null) {
				itemname = slot.getFirst().get("name");
			}
				
		}
		if (itemname == null) {
			return false;
		}
		
		final int price = object.getInt("price");
		final String offererName = object.get("offererName");
		component = new OrderPanel(itemname, price);
		final OrderPanelMouseListener listener = new OrderPanelMouseListener(new AcceptTradeOfferAction(itemname, price, offererName));
		

		component.addMouseListener(listener);
		
		return true;
	}

	public void deleted() {
		
	}

	public void modifiedAdded(final RPObject changes) {
		
	}

	public void modifiedDeleted(final RPObject changes) {
		
	}
	
	public Component getComponent() {
		return component;
		
	}

	/**
	 * @return the object
	 */
	public RPObject getObject() {
		return object;
	}

}
