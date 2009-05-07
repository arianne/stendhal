package games.stendhal.client.gui.tradingcenter;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;

class TradePanel {
	
	private final List<OrderPanelController> opcs = new LinkedList<OrderPanelController>();

	protected void add(final OrderPanelController orderPanelController) {
		this.opcs.add(orderPanelController);
	}

	protected boolean contains(final RPObject object) {
		for (final OrderPanelController opc : this.opcs) {
			if (opc.getObject().equals(object)) {
				return true;
			}
		}
		return false;
	}

}
