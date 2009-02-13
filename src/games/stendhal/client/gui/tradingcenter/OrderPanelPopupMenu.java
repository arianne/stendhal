package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.gui.styled.Style;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class OrderPanelPopupMenu extends JPopupMenu {

	public OrderPanelPopupMenu(final Style woodStyle, final String offerer, final boolean enabled) {
		final JMenuItem acceptItem = new JMenuItem("Accept");
		acceptItem.addActionListener(new AcceptTradeOfferAction());
		this.add(acceptItem);
	}

}
