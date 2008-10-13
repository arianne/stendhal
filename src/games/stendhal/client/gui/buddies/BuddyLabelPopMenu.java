package games.stendhal.client.gui.buddies;


import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.swing.StyledJPopupMenu;

@SuppressWarnings("serial")
public class BuddyLabelPopMenu extends StyledJPopupMenu {

	public BuddyLabelPopMenu(final Style style, final String buddyName, final boolean online) {
		super(style, buddyName);
		if (online) {
			createOnlineMenu(buddyName);
		} else {
			createOfflineMenu(buddyName);
		}
		
		RemoveBuddyMenuItem removeBuddyMenuItem = new RemoveBuddyMenuItem(buddyName);
		this.add(removeBuddyMenuItem);
		removeBuddyMenuItem.addActionListener(new RemovebuddyAction(buddyName));
	}

	private void createOfflineMenu(final String buddyName) {
		LeaveMessageBuddyMenuItem leaveMessageBuddyMenuItem = new LeaveMessageBuddyMenuItem(buddyName);
		this.add(leaveMessageBuddyMenuItem);
		leaveMessageBuddyMenuItem.addActionListener(new LeaveBuddyMessageAction(buddyName));
		
	}

	private void createOnlineMenu(final String buddyName) {
		TalkBuddyMenuItem talkBuddyMenuItem = new TalkBuddyMenuItem(buddyName);
		this.add(talkBuddyMenuItem);
		talkBuddyMenuItem.addActionListener(new TalkBuddyAction(buddyName));
		
		
		WhereBuddyMenuItem whereBuddyMenuItem = new WhereBuddyMenuItem(buddyName);
		this.add(whereBuddyMenuItem);
		whereBuddyMenuItem.addActionListener(new WhereBuddyMessageAction(buddyName));
		
	}

}
