/*
 * @(#) games/stendhal/client/gui/j2d/entity/CarrotGrower2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.CarrotGrower;

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a grower.
 */
class CarrotGrower2DView extends GrainField2DView {
	/**
	 * Create a 2D view of a grower.
	 * 
	 * @param grower
	 *            The entity to render.
	 */
	public CarrotGrower2DView(final CarrotGrower grower) {
		super(grower);
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
		list.add(ActionType.PICK.getRepresentation());

		super.buildActions(list);
		list.remove(ActionType.HARVEST.getRepresentation());
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.PICK);
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
		case PICK:
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
