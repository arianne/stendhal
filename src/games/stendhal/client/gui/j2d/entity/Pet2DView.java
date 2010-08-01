/*
 * @(#) games/stendhal/client/gui/j2d/entity/Pet2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Pet;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

import java.util.List;

/**
 * The 2D view of a pet.
 */
class Pet2DView extends DomesticAnimal2DView {
	/**
	 * The weight that a pet becomes fat (big).
	 */
	protected static final int BIG_WEIGHT = 20;

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
		User user = User.get();
		Pet pet = (Pet) entity;
		if ((user != null) && user.hasPet()) {
			list.add(ActionType.OWN.getRepresentation());
		} else if ((pet != null) && (user.getPetID() == pet.getID().getObjectID())) {
			list.add(ActionType.LEAVE_PET.getRepresentation());
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
		case LEAVE_PET:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;
		default:
			super.onAction(at);
			break;
		}
	}


	/**
	 * gets the mouse cursor image to use for this entity
	 *
	 * @return StendhalCursor
	 */
	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.LOOK;
	}
}
