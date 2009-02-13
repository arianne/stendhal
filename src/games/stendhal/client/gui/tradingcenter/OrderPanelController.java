package games.stendhal.client.gui.tradingcenter;

import java.awt.Component;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import games.stendhal.client.ObjectChangeListener;
import games.stendhal.client.entity.Item;

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
		String playerName = object.get("offerer");
		component = new OrderPanel(playerName, itemname, price);
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
