/*
 * @(#) games/stendhal/client/gui/j2d/entity/Box2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import java.util.List;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Box;
import marauroa.common.game.RPAction;

/**
 * The 2D view of a box.
 */
class Box2DView extends Item2DView {
	/**
	 * Create a 2D view of a box.
	 * 
	 * @param box
	 *            The entity to render.
	 */
	public Box2DView(final Box box) {
		super(box);
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
		list.add(ActionType.OPEN.getRepresentation());

		super.buildActions(list);
		list.remove(ActionType.USE.getRepresentation());
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.OPEN);
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
		case OPEN:
			final RPAction rpaction = new RPAction();

			rpaction.put("type", at.toString());
			getEntity().fillTargetInfo(rpaction);

			at.send(rpaction);
			break;

		default:
			super.onAction(at);
			break;
		}
	}
}
