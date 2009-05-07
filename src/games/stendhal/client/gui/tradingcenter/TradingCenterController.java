package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.PerceptionToObject;
import marauroa.common.game.RPObject;

class TradingCenterController {

	private PerceptionToObject pto = new PerceptionToObject();
	private TradePanel tradePanel = new TradePanel();
	
	
	protected void onAdded(final RPObject object) {
		final OrderPanelController orderPanelController = new OrderPanelController(object); 
		pto.register(object, orderPanelController);
		tradePanel.add(orderPanelController);
	}

	protected boolean contains(final RPObject object) {
		return tradePanel.contains(object);
	}

}
