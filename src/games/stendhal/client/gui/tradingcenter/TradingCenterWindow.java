package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.gui.wt.InternalManagedDialog;

import javax.swing.JComponent;


public class TradingCenterWindow extends InternalManagedDialog {

	private final JComponent panel;

	public TradingCenterWindow(final String name, final String title) {
		super(name, title);
		panel = new TradePanel();
		init();
	}

	private void init() {
		setContent(panel);
		setVisible(true);
	}

}
