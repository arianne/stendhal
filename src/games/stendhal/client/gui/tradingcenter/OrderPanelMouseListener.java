package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

final class OrderPanelMouseListener extends MouseAdapter {

	private final AcceptTradeOfferAction acceptAction;

	protected OrderPanelMouseListener(final AcceptTradeOfferAction acceptTradeOfferAction) {
		this.acceptAction = acceptTradeOfferAction;
	}

	public void mousePressed(final MouseEvent e) {
		this.maybeShowPopup(e);
	}

	public void mouseReleased(final MouseEvent e) {
		this.maybeShowPopup(e);
	}

	private void maybeShowPopup(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			final JPopupMenu popup = new OrderPanelPopupMenu(new WoodStyle(), acceptAction);
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}


}
