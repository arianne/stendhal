package games.stendhal.client.gui.wt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPopupMenu;

/**
 * This is the command list of any entities
 * 
 * @author mtotz
 */
public class CommandList extends StyledJPopupMenu {
	/** the entity associated with the command list */
	private Entity entity;

	/** the client */
	private StendhalClient client;

	/** This flag will be true of the object is contained inside another one */
	private boolean contained;

	/** In case the item is contained the base object that contain it */
	private String baseObject;

	/**
	 * In case the item is contained the slot of the base object that contains
	 * it
	 */
	private String baseSlot;

	/** creates a new CommandList */
	public CommandList(String name, String [] items, StendhalClient client,
	 Entity entity) {
		super(WoodStyle.getInstance(), name);

		this.entity = entity;
		this.client = client;
		this.contained = false;

		populate(items);
	}


	protected void populate(String [] items) {
		ActionListener	listener;
		Icon		adminIcon;
		Icon		icon;
		String		label;


		listener = new ActionSelectedCB();
		adminIcon = new AdminIcon();

		for(String item : items) {
			if(item.startsWith("(*)")) {
				icon = adminIcon;
				label = item.substring(3);
			} else {
				icon = null;
				label = item;
			}

			JMenuItem mi = new JMenuItem(label, icon);
			mi.setActionCommand(item);
			mi.addActionListener(listener);
			add(mi);
		}
	}


	public void setContext(int baseObject, String baseSlot) {
		this.baseObject = Integer.toString(baseObject);
		this.baseSlot = baseSlot;
		this.contained = true;
	}

	/** an action has been chosen */
	protected void doAction(String command) {
		// tell the entity what happened
		if (contained) {
			entity.onAction(client, command, baseObject, baseSlot);
		} else {
			entity.onAction(client, command);
		}
	}

	//
	//

	/**
	 * Handle action selection.
	 */
	protected class ActionSelectedCB implements ActionListener {
		//
		// ActionListener
		//

		public void actionPerformed(ActionEvent ev) {
			doAction(ev.getActionCommand());
		}
	}


	/**
	 * A pretty icon to indicate an admin option.
	 * </p>
	 *
	 * <p>
	 * It looks something like:
	 * <pre>
	 *      :
	 *      :
	 *     ###
	 *   ::#:#::
	 *     ###
	 *      :
	 *      :
	 * </pre>
	 */
	protected static class AdminIcon implements Icon {
		//
		// Icon
		//

		public int getIconHeight() {
			return 7;
		}


		public int getIconWidth() {
			return 7;
		}


		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color	oldColor;

			oldColor = g.getColor();

			g.setColor(Color.yellow);
			g.drawLine(x + 3, y, x + 3, y + 6);
			g.drawLine(x, y + 3, x + 6, y + 3);

			g.setColor(Color.red);
			g.drawRect(x + 2, y + 2, 2, 2);

			g.setColor(oldColor);
		}
	}
}
