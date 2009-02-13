package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.ObjectChangeListener;

import java.awt.Component;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class OrderPanelController implements ObjectChangeListener{

	private Component component;

	public OrderPanelController(RPObject object) {
		initialize(object);
		
	}

	private boolean initialize(RPObject object) {
		String itemname = null;
		if (object.hasSlot("goods")) {
			RPSlot slot = object.getSlot("goods");
			if (slot.getFirst() != null) {
				itemname = slot.getFirst().get("name");
			}
				
		}
		if (itemname == null) {
			return false;
		}
		
		int price = object.getInt("price");
		component = new OrderPanel(itemname, price);
		OrderPanelMouseListener listener = new OrderPanelMouseListener(new AcceptTradeOfferAction(itemname,price));
		

		component.addMouseListener(listener);
		
		return true;
	}

	public void deleted() {
		
	}

	public void modifiedAdded(RPObject changes) {
		
	}

	public void modifiedDeleted(RPObject changes) {
		
	}
	
	public Component getComponent(){
		return component;
		
	}

}
