package games.stendhal.client.gui.wt;

import games.stendhal.client.gui.BareBonesBrowserLaunch;

/**
 * This is a 'button' command list
 * 
 * @author kymara
 */
class ButtonCommandList extends CommandList {
	
	private static final long serialVersionUID = -1607102841664745919L;
	
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
	protected ButtonCommandList(final String name, final String[] items) {
		super(name, items);
	}

	/** an action has been chosen. 
	 * @param command */
	protected void doAction(final String command) {
		// for now, just open a url
		BareBonesBrowserLaunch.openURL("http://stendhalgame.org/wiki/StendhalFAQ");
	}

}
