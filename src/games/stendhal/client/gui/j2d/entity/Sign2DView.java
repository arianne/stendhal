/*
 * @(#) games/stendhal/client/gui/j2d/entity/Sign2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Sign;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * The 2D view of a sign.
 */
class Sign2DView extends Entity2DView {
	private static Logger logger = Logger.getLogger(Sign2DView.class);

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
		ActionType actionType = getActionType();
		list.add(actionType.getRepresentation());

		super.buildActions(list);
		list.remove(ActionType.LOOK.getRepresentation());
	}

	@Override
	protected void buildRepresentation() {
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
		onAction(getActionType());
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
		case LOOK_CLOSELY:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;
		case READ:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	/**
	 * gets the ActionType for the requested action
	 *
	 * @return ActionType
	 */
	private ActionType getActionType() {
		String action = ((Sign) entity).getAction();
		if (action == null) {
			return ActionType.LOOK;
		}

		try {
			return ActionType.valueOf(action.toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException  e) {
			logger.error("Unknown action for sign: " + action);
			return ActionType.LOOK;
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
