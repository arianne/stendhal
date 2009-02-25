/*
 * @(#) games/stendhal/client/gui/j2d/entity/FishSource2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.FishSource;

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a fish source.
 */
class FishSource2DView extends AnimatedLoopEntity2DView {
	/**
	 * Create a 2D view of food.
	 * 
	 * @param fishSource
	 *            The entity to render.
	 */
	public FishSource2DView(final FishSource fishSource) {
		super(fishSource);
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
		list.add(ActionType.FISH.getRepresentation());

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
		 onAction(ActionType.FISH); 
	}
	  
	/** Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case FISH:
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
