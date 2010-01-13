/*
 * @(#) games/stendhal/client/gui/j2d/entity/Item2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;

/**
 * The 2D view of a spell.
 */
class Spell2DView extends Entity2DView {

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
		list.add(ActionType.USE.getRepresentation());

		super.buildActions(list);
	}

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation() {
		setSprite(SpriteStore.get()
				.getSprite(translate(getClassResourcePath())));
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
		return 7000;
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
		return "data/sprites/spells/" + name + ".png";
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
	 * Determine if this entity can be moved (e.g. via dragging).
	 * 
	 * @return <code>true</code> if the entity is movable.
	 */
	@Override
	public boolean isMovable() {
		return true;
	}

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.USE);
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
		case USE:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;


		default:
			super.onAction(at);
			break;
		}
	}
}
