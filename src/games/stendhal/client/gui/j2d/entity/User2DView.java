/*
 * @(#) games/stendhal/client/gui/j2d/entity/User2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.User;

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a user.
 */
class User2DView extends Player2DView {

	/**
	 * The user entity.
	 */
	private final User user;

	/**
	 * Create a 2D view of a user.
	 * 
	 * @param user
	 *            The entity to render.
	 */
	public User2DView(final User user) {
		super(user);

		this.user = user;

	}

	//
	// RPEntity2DView
	//

	/**
	 * Determine is the user can see this entity while in ghostmode.
	 * 
	 * @return <code>true</code> if the client user can see this entity while in
	 *         ghostmode.
	 */
	@Override
	protected boolean isVisibleGhost() {
		return true;
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

		list.remove(ActionType.ATTACK.getRepresentation());
		list.remove(ActionType.ADD_BUDDY.getRepresentation());
		list.remove(ActionType.PUSH.getRepresentation());

		list.add(ActionType.SET_OUTFIT.getRepresentation());
		// list.add(ActionType.JOIN_GUILD.getRepresentation());

		if (user.hasSheep()) {
			list.add(ActionType.LEAVE_SHEEP.getRepresentation());
		}

		if (user.hasPet()) {
			list.add(ActionType.LEAVE_PET.getRepresentation());
		}
	}

	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 * 
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 * @param gameScreen
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == Entity.PROP_POSITION) {
			StendhalUI.get().setPosition(user.getX(), user.getY(),
					GameScreen.get());
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
		RPAction rpaction;

		switch (at) {
		// case JOIN_GUILD:
		// StendhalUI.get().manageGuilds();
		// break;

		case SET_OUTFIT:
			StendhalUI.get().chooseOutfit();
			break;

		case LEAVE_SHEEP:
			rpaction = new RPAction();

			rpaction.put("type", at.toString());
			rpaction.put("species", "sheep");
			rpaction.put("target", -1); // HACK see server handler code

			at.send(rpaction);
			break;

		case LEAVE_PET:
			rpaction = new RPAction();

			rpaction.put("type", at.toString());
			rpaction.put("species", "pet");
			rpaction.put("target", -1); // HACK see server handler code

			at.send(rpaction);
			break;

		default:
			super.onAction(at);
			break;
		}
	}
}
