package games.stendhal.client.gui.wt.buddies;


import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class BuddyPopUpMenu extends JPopupMenu {
	public BuddyPopUpMenu(String buddyName, boolean enabled) {
		super(buddyName);
		JMenuItem mi;
		 ActionListener listener = new ActionSelectedCB(buddyName);
        if (enabled) {
        	mi = new JMenuItem("Talk");
        	mi.setActionCommand("talk");
        	mi.addActionListener(listener);
        	add(mi);

        	mi = new JMenuItem("Where");
        	mi.setActionCommand("where");
        	mi.addActionListener(listener);
        	add(mi);
        } else {
        	mi = new JMenuItem("Leave Message");
        	mi.setActionCommand("leave-message");
        	mi.addActionListener(listener);
        	add(mi);
        }

        mi = new JMenuItem("Remove");
        mi.setActionCommand("remove");
        mi.addActionListener(listener);
        add(mi);
	}

}
