/*
 * @(#) games/stendhal/client/entity/User2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.soundreview.SoundMaster;

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a user.
 */
public class User2DView extends Player2DView {

	/**
	 * The user entity.
	 */
	private User	user;


	/**
	 * Create a 2D view of a user.
	 *
	 * @param	user		The entity to render.
	 */
	public User2DView(final User user) {
		super(user);

		this.user = user;

		GameScreen.get().place(user.getX(), user.getY());
		GameScreen.get().center();
	}


	//
	// RPEntity2DView
	//

	/**
	 * Determine is the user can see this entity while in ghostmode.
	 *
	 * @return	<code>true</code> if the client user can see this
	 *		entity while in ghostmode.
	 */
	@Override
	protected boolean isVisibleGhost() {
		return true;
	}


	//
	// Entity2DView
	//

	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);

		list.remove(ActionType.ATTACK.getRepresentation());
		list.remove(ActionType.ADD_BUDDY.getRepresentation());

	   	list.add(ActionType.SET_OUTFIT.getRepresentation());

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
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property)
	{
		super.entityChanged(entity, property);

		if(property == Entity.PROP_POSITION) {
			GameScreen.get().place(user.getX(), user.getY());
		}
	}


	//
	// EntityView
	//

	@Override
	public void onAction(final ActionType at) {
		RPAction rpaction;


		switch (at) {
//			case JOIN_GUILD:
//				StendhalUI.get().manageGuilds();
//				break;

			case SET_OUTFIT:
				StendhalUI.get().chooseOutfit();
				break;

			case LEAVE_SHEEP:
				rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", -1);

				at.send(rpaction);
				break;

			case LEAVE_PET:
				rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", -1);

				at.send(rpaction);

				//TODO: Move to sheep reaction, not user action
				SoundMaster.play("sheep-2.wav", user.getX(), user.getY());
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
