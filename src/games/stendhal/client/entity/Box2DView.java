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

	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);

		list.remove(ActionType.USE.getRepresentation());
		list.add(ActionType.OPEN.getRepresentation());
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
