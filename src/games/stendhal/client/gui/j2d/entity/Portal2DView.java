/*
 * @(#) games/stendhal/client/gui/j2d/entity/Portal2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Portal;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

import java.util.List;

/**
 * The 2D view of a portal.
 */
class Portal2DView extends InvisibleEntity2DView {

	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 * 
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		if (!((Portal) entity).isHidden()) {
			list.add(ActionType.USE.getRepresentation());

			super.buildActions(list);
			list.remove(ActionType.LOOK.getRepresentation());
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		if (!((Portal) entity).isHidden()) {
			onAction(ActionType.USE);
		}
	}

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case USE:

			at.send(at.fillTargetInfo(((Portal) entity).getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	@Override
	public boolean isInteractive() {
		return false;
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.PORTAL;
	}

}
