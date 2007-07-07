/*
 * @(#) games/stendhal/client/entity/Box2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a box.
 */
public class Box2DView extends Item2DView {
	/**
	 * Create a 2D view of a box.
	 *
	 * @param	box		The entity to render.
	 */
	public Box2DView(final Box box) {
		super(box);
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
		list.add(ActionType.OPEN.getRepresentation());

		super.buildActions(list);
		list.remove(ActionType.USE.getRepresentation());
	}


	//
	// EntityView
	//

	@Override
	public void onAction() {
		onAction(ActionType.OPEN);
	}


	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case OPEN:
				RPAction rpaction = new RPAction();

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
