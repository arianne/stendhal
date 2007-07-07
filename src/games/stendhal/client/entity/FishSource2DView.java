/*
 * @(#) games/stendhal/client/entity/FishSource2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a fish source.
 */
public class FishSource2DView extends AnimatedLoopEntity2DView {
	private static final int FRAME_COUNT = 32;


	/**
	 * Create a 2D view of food.
	 *
	 * @param	fishSource	The entity to render.
	 */
	public FishSource2DView(final FishSource fishSource) {
		super(fishSource, FRAME_COUNT);
	}


	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions.
	 * <strong>NOTE: The first entry should be the default.</strong>
	 *
	 * @param	list		The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.FISH.getRepresentation());

		super.buildActions(list);
	}


	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 *
	@Override
	public void onAction() {
		onAction(ActionType.FISH);
	}


	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 * @param	params		The parameters.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case FISH:
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
