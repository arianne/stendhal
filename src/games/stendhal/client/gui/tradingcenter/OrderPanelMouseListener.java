package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

public final class OrderPanelMouseListener extends MouseAdapter {

	public void mousePressed(final MouseEvent e) {
		this.maybeShowPopup(e);
	}

	public void mouseReleased(final MouseEvent e) {
		this.maybeShowPopup(e);
	}

	private void maybeShowPopup(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			final JPopupMenu popup = new OrderPanelPopupMenu(new WoodStyle(), e.getComponent().getName(), e.getComponent().isEnabled());
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}
