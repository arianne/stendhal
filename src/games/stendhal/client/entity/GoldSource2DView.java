/*
 * @(#) games/stendhal/client/entity/GoldSource2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import marauroa.common.game.RPAction;

/**
 * The 2D view of a gold source.
 */
public class GoldSource2DView extends AnimatedLoopEntity2DView {
	private static final int FRAME_COUNT = 32;


	/**
	 * Create a 2D view of a gold source.
	 *
	 * @param	goldSource	The entity to render.
	 */
	public GoldSource2DView(final GoldSource goldSource) {
		super(goldSource, FRAME_COUNT);
	}


	//
	// EntityView
	//

	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 * @param	params		The parameters.
	 */
	@Override
	public void onAction(final ActionType at, final String... params) {
		switch (at) {
			case PROSPECT:
				RPAction rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", getEntity().getID().getObjectID());

				at.send(rpaction);
				break;

			default:
				super.onAction(at, params);
				break;
		}
	}
}
