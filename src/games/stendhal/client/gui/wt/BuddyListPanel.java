/**
 * @(#) src/games/stendhal/client/gui/wt/BuddyListPanel.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.gui.ClientPanel;
import games.stendhal.client.gui.MouseHandlerAdapter;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A panel representing a buddy list.
 */
@SuppressWarnings("serial")
public final class BuddyListPanel extends ClientPanel {

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
	 * Create a buddy list panel.
	 */
	public BuddyListPanel(StendhalUI ui) {
		super("Buddy List", 100, 100);

		this.ui = ui;

		SpriteStore st = SpriteStore.get();
		online = st.getSprite("data/gui/buddy_online.png");
		offline = st.getSprite("data/gui/buddy_offline.png");

		buddies = new LinkedList<Entry>();

		setPreferredSize(new Dimension(132, 1));
		addMouseListener(new MouseClickCB());

		updateList();
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
		synchronized (buddies) {
			buddies.clear();

    		for (String key : buddy) {
    			if (!key.startsWith("_")) {
    				continue;
    			}

    			buddies.add(new Entry(key.substring(1), buddy.getInt(key) != 0));
    		}
		}

		int height = buddies.size() * 20 + 3;

		if (height != getHeight()) {
			setClientSize(132, height);
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
    public void paint(Graphics g) {
		super.paint(g);

		Point clnt = getClientPos();

		int y = clnt.y;

		synchronized (buddies) {
    		for (Entry entry : buddies) {
    			if (entry.isOnline()) {
    				g.setColor(Color.GREEN);
    				online.draw(g, clnt.x + 3, 2 + y);
    			} else {
    				g.setColor(Color.RED);
    				offline.draw(g, clnt.x + 3, 2 + y);
    			}

    			g.drawString(entry.getName(), clnt.x + 24, 16 + y);

    			y += 20;
    		}
		}
	}


	private class MouseClickCB extends MouseHandlerAdapter {
		/**
		 * Handle popup menus.
		 */
		@Override
		public void onPopup(MouseEvent e) {
			int i = (e.getY()-getClientPos().y) / 20;

			synchronized (buddies) {
	    		if ((i < 0) || (i >= buddies.size())) {
	    			return;
	    		}

	    		Entry entry = buddies.get(i);

	    		JPopupMenu menu = new JPopupMenu(entry.getName());
//	    		JPopupMenu menu = new StyledJPopupMenu(WoodStyle.getInstance(), entry.getName());
	    		ActionListener listener = new ActionSelectedCB(entry.getName());

				JMenuItem mi;

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

	            menu.show(e.getComponent(), e.getX(), e.getY());
			}
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
	 * Handle a chosen popup item.
	 *
	 * @param command
	 *            The command mnemonic selected.
	 * @param buddieName
	 *            The buddy name to act on.
	 */
	protected void doAction(String command, String buddieName) {
		StendhalClient client = ui.getClient();

		if ("talk".equals(command)) {
			/*
			 * Compatibility to grandfathered accounts with spaces. New accounts
			 * cannot contain spaces.
			 */
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			ui.setChatLine("/tell " + buddieName + " ");
		} else if ("leave-message".equals(command)) {
			/*
			 * Compatibility to grandfathered accounts with spaces. New accounts
			 * cannot contain spaces.
			 */
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			ui.setChatLine("/msg postman tell " + buddieName + " ");
		} else if ("where".equals(command)) {
			RPAction where = new RPAction();
			where.put("type", "where");
			where.put("target", buddieName);
			client.send(where);
		} else if ("remove".equals(command)) {
			RPAction where = new RPAction();
			where.put("type", "removebuddy");
			where.put("target", buddieName);
			client.send(where);
		}
	}

}
