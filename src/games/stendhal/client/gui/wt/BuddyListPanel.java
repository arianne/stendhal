/**
 * @(#) src/games/stendhal/client/gui/wt/BuddyListPanel.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPopupMenu;
import games.stendhal.client.gui.styled.swing.StyledJPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPSlot;

/**
 * 
 */
public class BuddyListPanel extends StyledJPanel {
	private Sprite		online;
	private Sprite		offline;
	private List<Entry>	buddies;


	public BuddyListPanel() {
		super(WoodStyle.getInstance());

		setPreferredSize(new Dimension(132, 20));

		SpriteStore st = SpriteStore.get();
		online = st.getSprite("data/gui/buddy_online.png");
		offline = st.getSprite("data/gui/buddy_offline.png");

		buddies = new LinkedList<Entry>();

		addMouseListener(new MouseClickCB());
	}


	public void updateList() {
		RPObject object = StendhalClient.get().getPlayer();

		if(object != null)
			updateList(object);
	}


	protected void updateList(RPObject object) {
		RPSlot slot = object.getSlot("!buddy");
		RPObject buddy = slot.getFirst();

		buddies.clear();

		for (String key : buddy) {
			if(!key.startsWith("_"))
				continue;

			buddies.add(
				new Entry(
					key.substring(1),
					buddy.getInt(key) != 0));
		}

		int height = buddies.size() * 20 + 3;

		if(height != getHeight())
		{
			setPreferredSize(new Dimension(132, height));

			/*
			 * Tell the parent to re-pack() itself
			 */
			putClientProperty("size-change", new Integer(height));
		}
	}


	protected void doPopup(Component comp, int x, int y) {
		JMenuItem	mi;


		int i = y / 20;

		if((i < 0) || (i >= buddies.size()))
			return;

		Entry entry = buddies.get(i);

		StyledJPopupMenu menu =
			new StyledJPopupMenu(
				WoodStyle.getInstance(), entry.getName());

		ActionListener listener =
			new ActionSelectedCB(entry.getName());

		if(entry.isOnline()) {
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
	 */
	protected void doAction(String command, String buddieName) {
		StendhalClient client = StendhalClient.get();

		if (command.equals("talk")) {
			/*
			 * Compatibility to grandfathered accounts with a ' '.
			 * New accounts cannot contain a spaces.
			 */
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			client.getTextLineGUI().setText(
				"/tell " + buddieName + " ");
		} else if (command.equals("leave-message")) {
			/*
			 * Compatibility to grandfathered accounts with a ' '.
			 * New accounts cannot contain a spaces.
			 */
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			client.getTextLineGUI().setText(
				"/msg postman tell " + buddieName + " ");
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

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);


		int y = 0;

		for(Entry entry : buddies) {
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
		protected String	name;
		protected boolean	online;


		public Entry(String name, boolean online) {
			this.name = name;
			this.online = online;
		}


		//
		// Entry
		//

		public String getName() {
			return name;
		}


		public boolean isOnline() {
			return online;
		}
	}


	/**
	 * Handle action selections.
	 */
	protected class ActionSelectedCB implements ActionListener {
		/**
		 * The buddy to act on.
		 */
		protected String	buddy;


		/**
		 * Create a listener for action items.
		 *
		 *
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
	 * Handle mouse clicks
	 */
	protected class MouseClickCB extends MouseAdapter {
		//
		// MouseListener
		//

		public void mousePressed(MouseEvent ev) {
			if(ev.isPopupTrigger()) {
				doPopup(
					ev.getComponent(),
					ev.getX(),
					ev.getY());
			}
		}


		public void mouseReleased(MouseEvent ev) {
			if(ev.isPopupTrigger()) {
				doPopup(
					ev.getComponent(),
					ev.getX(),
					ev.getY());
			}
		}
	}
}
