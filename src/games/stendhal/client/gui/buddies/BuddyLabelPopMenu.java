package games.stendhal.client.gui.buddies;


import games.stendhal.client.entity.User;
import games.stendhal.client.gui.styled.Style;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
class BuddyLabelPopMenu extends JPopupMenu {

	protected BuddyLabelPopMenu(final Style style, final String buddyName, final boolean online) {
		super(buddyName);
		if (online) {
			createOnlineMenu(buddyName);
		} else {
			createOfflineMenu(buddyName);
		}
		
		JMenuItem removeBuddyMenuItem = new JMenuItem("Remove");
		this.add(removeBuddyMenuItem);
		removeBuddyMenuItem.addActionListener(new RemovebuddyAction(buddyName));
	}
	
	// this one will fill into the chatline : /tell postman tell buddyName 
	// and then you type the message
	private void createOfflineMenu(final String buddyName) {
		JMenuItem leaveMessageBuddyMenuItem = new JMenuItem("Leave Message");
		this.add(leaveMessageBuddyMenuItem);
		leaveMessageBuddyMenuItem.addActionListener(new LeaveBuddyMessageAction(buddyName));
		
	}

	private void createOnlineMenu(final String buddyName) {
		
		// this one will fill into the chatline : /tell buddyName 
		// and then you type the message
		JMenuItem talkBuddyMenuItem = new JMenuItem("Talk");
		this.add(talkBuddyMenuItem);
		talkBuddyMenuItem.addActionListener(new TalkBuddyAction(buddyName));
		
		
		JMenuItem whereBuddyMenuItem = new JMenuItem("Where");
		this.add(whereBuddyMenuItem);
		whereBuddyMenuItem.addActionListener(new WhereBuddyAction(buddyName));
		
		if (User.isAdmin()) {
			JMenuItem teleportToBuddyMenuItem = new JMenuItem("(*)Teleport To");
			this.add(teleportToBuddyMenuItem);
			teleportToBuddyMenuItem.addActionListener(new TeleportToBuddyAction(buddyName));
		}
		
	}

}
