package games.stendhal.client.gui.buddies;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
class BuddyLabel extends JLabel {

	/**
	 * The online icon image.
	 */
	private static ImageIcon onlineIcon = new ImageIcon(BuddyLabel.class.getClassLoader().getResource("data/gui/buddy_online.png"));

	/**
	 * The offline icon image.
	 */
	private static ImageIcon offlineIcon = new ImageIcon(BuddyLabel.class.getClassLoader().getResource("data/gui/buddy_offline.png"));

	void setOnline(final boolean online) {
			this.setEnabled(online);
			paintImmediately(getVisibleRect());
	}

	public BuddyLabel() {
		super();
		initialize();
		this.setText("bobbele");
	}

	private BuddyLabel(final String name) {
		this();
		setName(name);
		setText(name);
	}

	protected BuddyLabel(final String name, final boolean isOnline) {
		this(name);
		setOnline(isOnline);
	}

	/**
	 * This method initializes icons, foreground and size.
	 *
	 */
	private void initialize() {
		this.setOpaque(false);
		this.setIcon(onlineIcon);
		this.setDisabledIcon(offlineIcon);
		this.setForeground(Color.GREEN);
		this.setSize(new Dimension(200, 30));
		this.addMouseListener(new BuddyLabelMouseListener());
	}
}
