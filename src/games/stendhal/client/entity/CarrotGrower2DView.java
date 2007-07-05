/*
 * @(#) games/stendhal/client/entity/CarrotGrower2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a grower.
 */
public class CarrotGrower2DView extends GrainField2DView {
	/**
	 * Create a 2D view of a grower.
	 *
	 * @param	grower		The entity to render.
	 */
	public CarrotGrower2DView(final CarrotGrower grower) {
		super(grower);
	}


	//
	// Entity2DView
	//

	@Override
	protected void buildActions(final List<String> list) {

		super.buildActions(list);

		list.remove(ActionType.HARVEST.getRepresentation());

		list.add(ActionType.PICK.getRepresentation());
	}


	//
	// EntityView
	//

	@Override
	public void onAction() {
		onAction(ActionType.PICK);
	}


	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case PICK:
				RPAction rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", getEntity().getID().getObjectID());

				at.send(rpaction);
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
