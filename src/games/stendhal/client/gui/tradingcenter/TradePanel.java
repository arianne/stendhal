package games.stendhal.client.gui.tradingcenter;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import marauroa.common.game.RPObject;

public class TradePanel extends JPanel {
	
	private static final long serialVersionUID = 1067639200909399350L;
	
	private final List<OrderPanelController> opcs = new LinkedList<OrderPanelController>();

	/**
	 * This method initializes 
	 * 
	 */
	public TradePanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
			
	}

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
