/**
 * @(#) src/games/stendhal/client/gui/wt/BuddyListPanel.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPopupMenu;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A panel representing a buddy list.
 */
public class BuddyListPanel extends JPanel {
	private static final long serialVersionUID = -1607102841664745919L;

	/**
	 * The UI.
	 */
	protected StendhalUI ui;

	/**
	 * The online icon image.
	 */
	private Sprite online;

	/**
	 * The offline icon image.
	 */
	private Sprite offline;

	/**
	 * A list of buddies.
	 */
	private List<Entry> buddies;

	/**
	 * Create a buddy list panel.
	 */
	public BuddyListPanel(StendhalUI ui) {
		this.ui = ui;

		setOpaque(false);

		SpriteStore st = SpriteStore.get();
		online = st.getSprite("data/gui/buddy_online.png");
		offline = st.getSprite("data/gui/buddy_offline.png");

		buddies = new LinkedList<Entry>();

		setPreferredSize(new Dimension(132, 1));
		addMouseListener(new MouseClickCB());
	}

	/**
	 * Rebuild the buddy list. Note: This needs to be called when updates are
	 * [possibly] needed.
	 */
	public void updateList() {
		RPObject object = StendhalClient.get().getPlayer();

		if (object != null) {
			RPSlot slot = object.getSlot("!buddy");
			updateList(slot.getFirst());
		}
	}

	/**
	 * Rebuild the buddy list from a list object.
	 * 
	 * @param buddy
	 *            The buddy list object.
	 */
	protected void updateList(RPObject buddy) {
		buddies.clear();

		for (String key : buddy) {
			if (!key.startsWith("_")) {
				continue;
			}

			buddies.add(new Entry(key.substring(1), buddy.getInt(key) != 0));
		}

		int height = buddies.size() * 20 + 3;

		if (height != getHeight()) {
			setPreferredSize(new Dimension(132, height));

			/*
			 * Tell the parent to re-pack() itself
			 * 
			 * TODO Maybe there's a better way (without introducing
			 * dependencies/code-coupling) XXX
			 */
			putClientProperty("size-change", new Integer(height));
		}
	}

	/**
	 * Handle a popup click.
	 * 
	 * @param comp
	 *            The component clicked on.
	 * @param x
	 *            The X coordinate of the mouse click.
	 * @param y
	 *            The X coordinate of the mouse click.
	 */
	protected void doPopup(Component comp, int x, int y) {
		JMenuItem mi;

		int i = y / 20;

		if ((i < 0) || (i >= buddies.size())) {
			return;
		}

		Entry entry = buddies.get(i);

		StyledJPopupMenu menu = new StyledJPopupMenu(WoodStyle.getInstance(),
				entry.getName());

		ActionListener listener = new ActionSelectedCB(entry.getName());

		if (entry.isOnline()) {
			mi = new JMenuItem("Talk");
			mi.setActionCommand("talk");
			mi.addActionListener(listener);
			menu.add(mi);

			mi = new JMenuItem("Where");
			mi.setActionCommand("where");
			mi.addActionListener(listener);
			menu.add(mi);
		} else {
			mi = new JMenuItem("Leave Message");
			mi.setActionCommand("leave-message");
			mi.addActionListener(listener);
			menu.add(mi);
		}

		mi = new JMenuItem("Remove");
		mi.setActionCommand("remove");
		mi.addActionListener(listener);
		menu.add(mi);

		menu.show(comp, x, y);
	}

	/**
	 * Handle a choosen popup item.
	 * 
	 * @param command
	 *            The command mnemonic selected.
	 * @param buddieName
	 *            The buddy name to act on.
	 */
	protected void doAction(String command, String buddieName) {
		StendhalClient client = ui.getClient();

		if (command.equals("talk")) {
			/*
			 * Compatibility to grandfathered accounts with spaces. New accounts
			 * cannot contain spaces.
			 */
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			ui.setChatLine("/tell " + buddieName + " ");
		} else if (command.equals("leave-message")) {
			/*
			 * Compatibility to grandfathered accounts with spaces. New accounts
			 * cannot contain spaces.
			 */
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			ui.setChatLine("/msg postman tell " + buddieName + " ");
		} else if (command.equals("where")) {
			RPAction where = new RPAction();
			where.put("type", "where");
			where.put("target", buddieName);
			client.send(where);
		} else if (command.equals("remove")) {
			RPAction where = new RPAction();
			where.put("type", "removebuddy");
			where.put("target", buddieName);
			client.send(where);
		}
	}

	//
	// JComponent
	//

	/**
	 * Render the buddy list. Eventually this will be replaced by a JList that
	 * can be scrolled (for popular players with many friends).
	 * 
	 * @param g
	 *            The graphics context.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int y = 0;

		for (Entry entry : buddies) {
			if (entry.isOnline()) {
				g.setColor(Color.GREEN);
				online.draw(g, 3, 2 + y);
			} else {
				g.setColor(Color.RED);
				offline.draw(g, 3, 2 + y);
			}

			g.drawString(entry.getName(), 24, 16 + y);

			y += 20;
		}
	}

	//
	//

	/**
	 * A buddy entry.
	 */
	protected static class Entry {

		/**
		 * The buddy name.
		 */
		protected String name;

		/**
		 * Whether the buddy is online.
		 */
		protected boolean online;

		/**
		 * Create a buddy entry.
		 * 
		 * @param name
		 *            The buddy name.
		 * @param online
		 *            Whether the buddy is online.
		 */
		public Entry(String name, boolean online) {
			this.name = name;
			this.online = online;
		}

		//
		// Entry
		//

		/**
		 * Get the buddy name.
		 * 
		 * @return The buddy name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Determine is the buddy is online.
		 * 
		 * @return <code>true</code> if online.
		 */
		public boolean isOnline() {
			return online;
		}
	}

	/**
	 * Handle action selection.
	 */
	protected class ActionSelectedCB implements ActionListener {

		/**
		 * The buddy to act on.
		 */
		protected String buddy;

		/**
		 * Create a listener for action items.
		 * 
		 * @param buddy
		 *            The buddy to act on.
		 */
		public ActionSelectedCB(String buddy) {
			this.buddy = buddy;
		}

		//
		// ActionListener
		//

		public void actionPerformed(ActionEvent ev) {
			doAction(ev.getActionCommand(), buddy);
		}
	}

	/**
	 * Handle mouse clicks.
	 */
	protected class MouseClickCB extends MouseAdapter {

		//
		// MouseListener
		//

		/**
		 * Track mouse presses.
		 */
		@Override
		public void mousePressed(MouseEvent ev) {
			if (ev.isPopupTrigger()) {
				doPopup(ev.getComponent(), ev.getX(), ev.getY());
			}
		}

		/**
		 * Track mouse releases.
		 */
		@Override
		public void mouseReleased(MouseEvent ev) {
			if (ev.isPopupTrigger()) {
				doPopup(ev.getComponent(), ev.getX(), ev.getY());
			}
		}
	}
}
