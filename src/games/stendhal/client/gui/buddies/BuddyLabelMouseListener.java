/**
 * 
 */
package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

final class BuddyLabelMouseListener extends MouseAdapter {
	
	
	@Override
	public void mousePressed(final MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		maybeShowPopup(e);
	}

    private void maybeShowPopup(final MouseEvent e) {
        if (e.isPopupTrigger()) {
        	final JPopupMenu popup = new BuddyLabelPopMenu(new WoodStyle(), e.getComponent().getName(), e.getComponent().isEnabled());
        	popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
