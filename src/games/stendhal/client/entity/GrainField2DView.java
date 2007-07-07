/*
 * @(#) games/stendhal/client/entity/GrainField2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;

/**
 * The 2D view of a grain field.
 */
public class GrainField2DView extends StateEntity2DView {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Log4J.getLogger(RPEntity2DView.class);

	/**
	 * The grain field entity.
	 */
	private GrainField	grainField;

	/**
	 * The number of states.
	 */
	protected int		states;


	/**
	 * Create a 2D view of a grain field.
	 *
	 * @param	grainField	The entity to render.
	 */
	public GrainField2DView(final GrainField grainField) {
		super(grainField);

		this.grainField = grainField;

		states = 0;
	}


	//
	// GrainField2DView
	//

	/**
	 * Get the height.
	 *
	 * @return	The height in tile units.
	 */
	public double getHeight() {
		return grainField.getHeight();
	}


	/**
	 * Get the width.
	 *
	 * @return	The width in tile units.
	 */
	public double getWidth() {
		return grainField.getWidth();
	}


	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		double	height;
		double	width;
		String	clazz;


		height = getHeight();
		width = getWidth();

		clazz = grainField.getEntityClass();

		if(clazz == null)  {
			logger.warn("No entity class set");
			clazz = "grain_field";
		}

		SpriteStore store = SpriteStore.get();
		Sprite tiles = store.getSprite(translate(clazz));

		states = grainField.getMaximumRipeness() + 1;
		int imageStates = tiles.getHeight() / (int) (GameScreen.SIZE_UNIT_PIXELS * height);

		if(imageStates != states) {
			logger.warn("State count mismatch: " + imageStates + " != " + states);

			if(imageStates < states) {
				states = imageStates;
			}
		}

		for(int i = 0; i < states; i++) {
			map.put(new Integer(i), store.getSprite(tiles, 0, i, width, height));
		}
	}


	/**
	 * Get the current entity state.
	 *
	 * @return	The current state.
	 */
	@Override
	protected Object getState() {
		return new Integer(grainField.getRipeness());
	}


	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions.
	 * <strong>NOTE: The first entry should be the default.</strong>
	 *
	 * @param	list		The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.HARVEST.getRepresentation());

		super.buildActions(list);
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(
			getX(), getY(), getWidth(), getHeight());
        }

	/**
	 * Determines on top of which other entities this entity should be
	 * drawn. Entities with a high Z index will be drawn on top of ones
	 * with a lower Z index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return	The drawing index.
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
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property)
	{
		super.entityChanged(entity, property);

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		} else if(property == GrainField.PROP_RIPENESS) {
			stateChanged = true;
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
	 * @param	at		The action.
	 * @param	params		The parameters.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case HARVEST:
				RPAction rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", grainField.getID().getObjectID());

				at.send(rpaction);
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
