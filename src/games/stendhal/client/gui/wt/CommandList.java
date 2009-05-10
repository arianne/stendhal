package games.stendhal.client.gui.wt;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.wt.core.WtPopupMenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * This is the command list of any entities.
 * 
 * @author mtotz
 */
class CommandList extends WtPopupMenu {
	private static final long serialVersionUID = -1607102841664745919L;

	/** the entity associated with the command list. */
	private final EntityView view;

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
	protected CommandList(final String name, final String[] items,
			final EntityView view) {
		super(name);

		this.view = view;

		populate(items);
	}

	private void populate(final String[] items) {
		ActionListener listener;
		Icon adminIcon;
		Icon icon;
		String label;

		listener = new ActionSelectedCB();
		adminIcon = new AdminIcon();

		for (final String item : items) {
			if (item.startsWith("(*)")) {
				icon = adminIcon;
				label = item.substring(3);
			} else {
				icon = null;
				label = item;
			}

			final JMenuItem mi = createItem(label, icon);
			mi.setActionCommand(item);
			mi.addActionListener(listener);
			add(mi);
		}
	}

	/** an action has been chosen. 
	 * @param command */
	private void doAction(final String command) {
		// tell the entity what happened
		view.onAction(ActionType.getbyRep(command));
	}

	//
	//

	/**
	 * Handle action selection.
	 */
	private class ActionSelectedCB implements ActionListener {

		//
		// ActionListener
		//

		public void actionPerformed(final ActionEvent ev) {
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
	private static class AdminIcon implements Icon {

		public int getIconHeight() {
			return 7;
		}

		public int getIconWidth() {
			return 7;
		}

		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
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
