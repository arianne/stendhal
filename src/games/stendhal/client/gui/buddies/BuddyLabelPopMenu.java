package games.stendhal.client.gui.buddies;


import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.swing.StyledJPopupMenu;

import javax.swing.JMenuItem;

@SuppressWarnings("serial")
class BuddyLabelPopMenu extends StyledJPopupMenu {

	protected BuddyLabelPopMenu(final Style style, final String buddyName, final boolean online) {
		super(style, buddyName);
		if (online) {
			createOnlineMenu(buddyName);
		} else {
			createOfflineMenu(buddyName);
		}
		
		JMenuItem removeBuddyMenuItem = new JMenuItem("Remove");
		this.add(removeBuddyMenuItem);
		removeBuddyMenuItem.addActionListener(new RemovebuddyAction(buddyName));
	}

	private void createOfflineMenu(final String buddyName) {
		JMenuItem leaveMessageBuddyMenuItem = new JMenuItem("Leave Message");
		this.add(leaveMessageBuddyMenuItem);
		leaveMessageBuddyMenuItem.addActionListener(new LeaveBuddyMessageAction(buddyName));
		
	}

	private void createOnlineMenu(final String buddyName) {
		JMenuItem talkBuddyMenuItem = new JMenuItem("Talk");
		this.add(talkBuddyMenuItem);
		talkBuddyMenuItem.addActionListener(new TalkBuddyAction(buddyName));
		
		
		JMenuItem whereBuddyMenuItem = new JMenuItem("Where");
		this.add(whereBuddyMenuItem);
		whereBuddyMenuItem.addActionListener(new WhereBuddyAction(buddyName));
		
	}

}
