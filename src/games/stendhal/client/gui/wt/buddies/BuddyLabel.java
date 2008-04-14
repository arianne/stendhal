package games.stendhal.client.gui.wt.buddies;

import games.stendhal.client.gui.MouseHandlerAdapter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class BuddyLabel extends JLabel {
	static final Icon online = new ImageIcon("data/gui/buddy_online.png");
	static final Icon offline = new ImageIcon("data/gui/buddy_offline.png");

	public BuddyLabel(String buddyName) {
		super(buddyName);
		setName(buddyName);
		setPreferredSize(new Dimension(100, 20));
		setIcon(online);
		setDisabledIcon(offline);
		setVisible(true);
		setEnabled(false);
		setForeground(Color.green);
		addMouseListener(new MouseHandlerAdapter() {
			@Override
			protected void onPopup(MouseEvent e) {

				JPopupMenu menu = new BuddyPopUpMenu(getName(), isEnabled());
				menu.show(e.getComponent(), e.getX() - 10, e.getY() - 10);
			}

		});

	}
}
