package games.stendhal.client.gui.tradingcenter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class OrderPanelMouseListener extends MouseAdapter {

	public void mousePressed(MouseEvent e) {
		this.maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		this.maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
