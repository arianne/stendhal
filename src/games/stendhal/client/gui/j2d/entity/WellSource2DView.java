/*
 * @(#) games/stendhal/client/gui/j2d/entity/GoldSource2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;

import java.util.List;

/**
 * The 2D view of a gold source.
 */
class WellSource2DView extends AnimatedLoopEntity2DView {

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
		list.add(ActionType.WISH.getRepresentation());

		super.buildActions(list);
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.WISH);
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
		case WISH:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}

	}
}
