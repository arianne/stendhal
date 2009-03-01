package games.stendhal.client.gui.tradingcenter;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;

public class TradePanel {
	
	private final List<OrderPanelController> opcs = new LinkedList<OrderPanelController>();

	public void add(OrderPanelController orderPanelController) {
		this.opcs.add(orderPanelController);
	}

	public boolean contains(RPObject object) {
		for (OrderPanelController opc : this.opcs) {
			if (opc.getObject().equals(object)) {
				return true;
			}
		}
		return false;
	}

}
