package games.stendhal.client.gui.wt;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.gui.j2d.entity.EntityView;

/**
 * This is the command list of any entities.
 * 
 * @author mtotz
 */
class EntityViewCommandList extends CommandList {
	
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
	protected EntityViewCommandList(final String name, final String[] items, final EntityView view) {
		super(name, items);
		this.view = view;

	}

	/** an action has been chosen. 
	 * @param command */
	protected void doAction(final String command) {
		// tell the entity what happened
		view.onAction(ActionType.getbyRep(command));
	}

}
