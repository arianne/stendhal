/*
 * @(#) games/stendhal/client/gui/j2d/entity/GrainField2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.GrainField;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The 2D view of a grain field.
 */
class GrainField2DView extends StateEntity2DView {
	
	/**
	 * The number of states.
	 */
	protected int states;
	
	/**
	 * Log4J.
	 */
	private static final Logger LOGGER = Logger.getLogger(RPEntity2DView.class);

	/**
	 * Create a 2D view of a grain field.
	 * 
	 * @param grainField
	 *            The entity to render.
	 */
	public GrainField2DView() {
		super();
		states = 0;
	}

	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 * 
	 * @param map
	 *            The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		int height;
		int width;
		String clazz;

		height = getHeight();
		width = getWidth();

		clazz = entity.getEntityClass();

		if (clazz == null) {
			LOGGER.warn("No entity class set");
			clazz = "grain_field";
		}

		final SpriteStore store = SpriteStore.get();
		final Sprite tiles = store.getSprite(translate(clazz.replace(" ", "_")));

		states = ((GrainField) entity).getMaximumRipeness() + 1;

		final int theight = tiles.getHeight();
		final int imageStates = theight / height;

		if (imageStates != states) {
			LOGGER.warn("State count mismatch: " + imageStates + " != "
					+ states);

			if (imageStates < states) {
				states = imageStates;
			}
		}

		int i = 0;

		for (int y = 0; y < theight; y += height) {
			map.put(Integer.valueOf(i++), store.getTile(tiles, 0, y, width,
					height));
		}
	}

	/**
	 * Get the current entity state.
	 * 
	 * @return The current state.
	 */
	@Override
	protected Object getState() {
		return Integer.valueOf(((GrainField) entity).getRipeness());
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
		list.add(ActionType.HARVEST.getRepresentation());

		super.buildActions(list);
	}

	/**
	 * Get the height.
	 * 
	 * @return The height (in pixels).
	 */
	@Override
	public int getHeight() {
		return (int) (entity.getHeight() * IGameScreen.SIZE_UNIT_PIXELS);
	}

	/**
	 * Get the width.
	 * 
	 * @return The width (in pixels).
	 */
	@Override
	public int getWidth() {
		return (int) (entity.getWidth() * IGameScreen.SIZE_UNIT_PIXELS);
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
		return 3000;
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
		} else if (property == GrainField.PROP_RIPENESS) {
			proceedChangedState();
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
		onAction(ActionType.HARVEST);
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
		case HARVEST:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.HARVEST;
	}

}
