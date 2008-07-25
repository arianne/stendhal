/*
 * @(#) games/stendhal/client/gui/j2d/entity/Sign2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Sign;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;

import marauroa.common.game.RPAction;

/**
 * The 2D view of a sign.
 */
class Sign2DView extends Entity2DView {

	/**
	 * Create a 2D view of a sign.
	 * 
	 * @param sign
	 *            The entity to render.
	 */
	public Sign2DView(final Sign sign) {
		super(sign);
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
		list.add(ActionType.READ.getRepresentation());

		super.buildActions(list);
		list.remove(ActionType.LOOK.getRepresentation());
	}

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation(final IGameScreen gameScreen) {
		String name = getClassResourcePath();

		if (name == null) {
			name = "default";
		}

		setSprite(SpriteStore.get().getSprite(translate(name)));
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 5000;
	}

	/**
	 * Translate a resource name into it's sprite image path.
	 * 
	 * @param name
	 *            The resource name.
	 * 
	 * @return The full resource name.
	 */
	@Override
	protected String translate(final String name) {
		return "data/sprites/signs/" + name + ".png";
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
	public void entityChanged(final IEntity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.READ);
	}

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case READ:
			final RPAction rpaction = new RPAction();
			rpaction.put("type", at.toString());
			entity.fillTargetInfo(rpaction);

			at.send(rpaction);
			break;

		/*
		 * String text = sign.getText();
		 * 
		 * Rectangle area = getArea();
		 * 
		 * screen.addText(area.x + (area.width / 2), area.y, text,
		 * NotificationType.RESPONSE, false);
		 * 
		 * if (text.contains("\n")) { // The sign's text has multiple lines. Add
		 * a linebreak after // "you read" so that it is easier readable.
		 * StendhalUI.get().addEventLine("You read:\n\"" + text + "\"",
		 * NotificationType.RESPONSE); } else {
		 * StendhalUI.get().addEventLine("You read: \"" + text + "\"",
		 * NotificationType.RESPONSE); } break;
		 */

		default:
			super.onAction(at);
			break;
		}
	}
}
