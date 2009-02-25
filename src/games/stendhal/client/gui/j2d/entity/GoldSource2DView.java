/*
 * @(#) games/stendhal/client/gui/j2d/entity/GoldSource2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.GoldSource;

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a gold source.
 */
class GoldSource2DView extends AnimatedLoopEntity2DView {
	/**
	 * Create a 2D view of a gold source.
	 * 
	 * @param goldSource
	 *            The entity to render.
	 */
	public GoldSource2DView(final GoldSource goldSource) {
		super(goldSource);
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
		list.add(ActionType.PROSPECT.getRepresentation());

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
		 onAction(ActionType.PROSPECT);
	}
	
	/** Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case PROSPECT:
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
