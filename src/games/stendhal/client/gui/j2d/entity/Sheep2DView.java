/*
 * @(#) games/stendhal/client/gui/j2d/entity/Sheep2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Sheep;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

import java.util.List;

/**
 * The 2D view of a sheep.
 */
class Sheep2DView extends DomesticAnimal2DView {
	/**
	 * The weight that a sheep becomes fat (big).
	 */
	protected static final int BIG_WEIGHT = 60;

	//
	// DomesticAnimal2DView
	//

	/**
	 * Get the weight at which the animal becomes big.
	 * 
	 * @return A weight.
	 */
	@Override
	protected int getBigWeight() {
		return BIG_WEIGHT;
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
		super.buildActions(list);
		if (!User.isNull()) {
			if (!User.get().hasSheep()) {
				list.add(ActionType.OWN.getRepresentation());
			} else if (User.get().getSheepID() == ((Sheep) entity).getID()
					.getObjectID()) {
				list.add(ActionType.LEAVE_SHEEP.getRepresentation());
			}
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case LEAVE_SHEEP:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.LOOK;
	}
	
}
