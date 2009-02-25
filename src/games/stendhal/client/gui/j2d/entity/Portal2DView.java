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

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a portal.
 */
class Portal2DView extends InvisibleEntity2DView {
	/**
	 * The portal entity.
	 */
	protected Portal portal;

	/**
	 * Create a 2D view of a portal.
	 * 
	 * @param portal
	 *            The entity to render.
	 */
	public Portal2DView(final Portal portal) {
		super(portal);

		this.portal = portal;
	}

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
		if (!portal.isHidden()) {
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
		if (!portal.isHidden()) {
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
			final RPAction rpaction = new RPAction();

			rpaction.put("type", at.toString());
			portal.fillTargetInfo(rpaction);

			at.send(rpaction);
			break;

		default:
			super.onAction(at);
			break;
		}
	}
}
