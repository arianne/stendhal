package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.gui.styled.Style;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class OrderPanelPopupMenu extends JPopupMenu {

	protected OrderPanelPopupMenu(final Style style, final AcceptTradeOfferAction acceptAction) {
		final JMenuItem acceptItem = new JMenuItem("Accept");
		acceptItem.addActionListener(acceptAction);
			this.add(acceptItem);
	}

}
