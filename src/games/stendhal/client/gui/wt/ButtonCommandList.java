package games.stendhal.client.gui.wt;

import games.stendhal.client.actions.SlashActionRepository;

/**
 * This is a 'button' command list
 * 
 * @author kymara
 */
class ButtonCommandList extends CommandList {
	
	private static final long serialVersionUID = -1607102841664745919L;
		
	/**
	 * Create an button command list.
	 * 
	 * @param name
	 *            The menu name (needed?).
	 * @param items
	 *            The action names.
	 */
	protected ButtonCommandList(final String name, final String[] items) {
		super(name, items);
	}

	/** an action has been chosen. 
	 * @param command */
	protected void doAction(String command) {
		// the commands from PopUpMenuOpener may have had spaces, strip these out
		command = command.replace(" ","");
		SlashActionRepository.get(command).execute(null, null);
	}

}
