package games.stendhal.client.gui.tradingcenter;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;

public class TradePanel {
	
	private final List<OrderPanelController> opcs = new LinkedList<OrderPanelController>();

	public void add(final OrderPanelController orderPanelController) {
		this.opcs.add(orderPanelController);
	}

	public boolean contains(final RPObject object) {
		for (final OrderPanelController opc : this.opcs) {
			if (opc.getObject().equals(object)) {
				return true;
			}
		}
		return false;
	}

}
