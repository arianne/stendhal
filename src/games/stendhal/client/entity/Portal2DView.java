/*
 * @(#) games/stendhal/client/entity/Portal2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a portal.
 */
public class Portal2DView extends InvisibleEntity2DView {
	/**
	 * The portal entity.
	 */
	protected Portal	portal;


	/**
	 * Create a 2D view of a portal.
	 *
	 * @param	portal		The entity to render.
	 */
	public Portal2DView(final Portal portal) {
		super(portal);

		this.portal = portal;
	}


	//
	// Entity2DView
	//

	@Override
	protected void buildActions(List<String> list) {
		// Do not call super method because we do not want the
		// Look menu until some nice text are there to be looked at.
		//
		// super.buildActions(list);

		if (!portal.isHidden()) {
			list.add(ActionType.USE.getRepresentation());
		}
	}


	//
	// EntityView
	//

	@Override
	public void onAction() {
		if(!portal.isHidden()) {
			onAction(ActionType.USE);
		}
	}


	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case USE:
				RPAction rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", portal.getID().getObjectID());

				at.send(rpaction);
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
