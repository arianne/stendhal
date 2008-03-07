package games.stendhal.client.gui.buddies;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

class BuddyLabel extends JLabel{

	/**
	 * The online icon image.
	 */
	private static ImageIcon onlineIcon = new ImageIcon("data/gui/buddy_online.png");

	/**
	 * The offline icon image.
	 */
	private static ImageIcon offlineIcon = new ImageIcon("data/gui/buddy_offline.png");

	void setOnline(boolean online) {
			this.setEnabled(online);
	}

	public BuddyLabel() {
		super();
		initialize();
		this.setText("bobbele");
	}

	public BuddyLabel(String name) {
		this();
		setText(name);
	}

	public BuddyLabel(String name, boolean isOnline) {
		this();
		setOnline(isOnline);
		setText(name);
	}

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setOpaque(false);
		this.setIcon(onlineIcon);
		this.setDisabledIcon(offlineIcon);
		this.setForeground(Color.GREEN);
		this.setSize(new Dimension(200, 30));
	}

}
