package games.stendhal.client.gui.bag;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupMenuListener extends MouseAdapter {
	public void mousePressed(final MouseEvent e) {
		this.maybeShowPopup(e);
	}

	public void mouseReleased(final MouseEvent e) {
		this.maybeShowPopup(e);
	}

	private void maybeShowPopup(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			createAndShowPopup(e);
		}
	}

	protected void createAndShowPopup(MouseEvent e) {
		
	}
}
