package games.stendhal.client.gui.wt;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.EntityView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * This is the command list of any entities.
 * 
 * @author mtotz
 */
@SuppressWarnings("serial")
public class CommandList extends JPopupMenu //StyledJPopupMenu
{

	/** the entity associated with the command list. */
	private EntityView view;

	/**
	 * Create an entity view command list.
	 * 
	 * @param name
	 *            The menu name (needed?).
	 * @param items
	 *            The action names.
	 * @param view
	 *            The entity view.
	 */
	public CommandList(final String name, final String[] items, final EntityView view) {
//		super(WoodStyle.getInstance(), name);
		super(name);

		this.view = view;

		populate(items);
	}


	/**
	 * Display the popup menu at the given position.
	 *
	 * @param pt
	 */
	public void display(MouseEvent e) {
    	show(e.getComponent(), e.getX(), e.getY());
	}

	protected void populate(String[] items) {
		ActionListener listener;
		Icon adminIcon;
		Icon icon;
		String label;

		listener = new ActionSelectedCB();
		adminIcon = new AdminIcon();

		for (String item : items) {
			if (item.startsWith("(*)")) {
				icon = adminIcon;
				label = item.substring(3);
			} else {
				icon = null;
				label = item;
			}

			JMenuItem mi = new JMenuItem(label, icon);//WtPopupMenu.createItem(label, icon);
			mi.setActionCommand(item);
			mi.addActionListener(listener);
			add(mi);
		}
	}

	/** an action has been chosen. */
	protected void doAction(String command) {
		// tell the entity what happened
		view.onAction(ActionType.getbyRep(command));
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
	 *  
	 * <p>
	 * It looks something like:
	 * 
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
		public int getIconHeight() {
			return 7;
		}

		public int getIconWidth() {
			return 7;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color oldColor;

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
