package games.stendhal.client.gui.wt.buddies;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import marauroa.common.game.RPAction;

/**
 * Handle action selection.
 */
public class ActionSelectedCB implements ActionListener {

	private static StendhalUI ui;
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
	/**
	 * Handle a chosen popup item.
	 *
	 * @param command
	 *            The command mnemonic selected.
	 * @param buddieName
	 *            The buddy name to act on.
	 */
	protected static void doAction(String command, String buddieName) {
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

	public static void setUI(StendhalUI ui) {
		ActionSelectedCB.ui = ui;
		
	}
}