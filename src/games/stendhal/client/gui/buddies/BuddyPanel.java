package games.stendhal.client.gui.buddies;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;

/**
 * JList that can show popup menues for buddies. Use <code>BuddyListModel</code>
 * with this.
 */
class BuddyPanel extends JList {
	/**
	 * The amount of pixels that popup menus will be shifted up and left from
	 * the clicking point.
	 */
	private static final int POPUP_OFFSET = 10;
	
	/**
	 * Create a new BuddyList.
	 * 
	 * @param model associated list model
	 */
	protected BuddyPanel(final BuddyListModel model) {
		super(model);
		setCellRenderer(new BuddyLabel());
		setOpaque(false);
		this.setFocusable(false);
		this.addMouseListener(new BuddyPanelMouseListener());
	}
	
	/**
	 * MouseListener for triggering the buddy list popup menus.
	 */
	private class BuddyPanelMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(final MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * Show the popup if the mouse even is a popup trigger for the platform.
		 * 
		 * @param e
		 */
		private void maybeShowPopup(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				int index = BuddyPanel.this.locationToIndex(e.getPoint());
				Object obj = BuddyPanel.this.getModel().getElementAt(index);
				if (obj instanceof Buddy) {
					Buddy buddy = (Buddy) obj;
					final JPopupMenu popup = new BuddyLabelPopMenu(buddy.getName(), buddy.isOnline());
					popup.show(e.getComponent(), e.getX() - POPUP_OFFSET, e.getY() - POPUP_OFFSET);
				}
			}
		}
	}
}
