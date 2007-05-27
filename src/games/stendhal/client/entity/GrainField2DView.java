/*
 * @(#) games/stendhal/client/entity/GrainField2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a grain field.
 */
public class GrainField2DView extends StateEntity2DView {
	/**
	 * The grain field entity.
	 */
	private GrainField	grainField;


	/**
	 * Create a 2D view of a grain field.
	 *
	 * @param	grainField	The entity to render.
	 */
	public GrainField2DView(final GrainField grainField) {
		super(grainField);

		this.grainField = grainField;
	}


	//
	// GrainField2DView
	//

	public double getHeight() {
		return grainField.getHeight();
	}


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
	protected void buildSprites(Map<Object, Sprite> map) {
		double	height;
		double	width;
		int	maxRipeness;
		String	clazz;


		height = getHeight();
		width = getWidth();
		maxRipeness = grainField.getMaximumRipeness();

		clazz = grainField.getEntityClass();

		if(clazz == null)  {
			clazz = "grain_field";
		}

		SpriteStore store = SpriteStore.get();
		Sprite tiles = store.getSprite(translate(clazz));

		for(int i = 0; i <= maxRipeness; i++) {
			map.put(new Integer(i), store.getSprite(tiles, 0, i, width, height));
		}
	}


	/**
	 * Get the current entity state.
	 *
	 * @return	The current state.
	 */
	@Override
	public Object getState() {
		return new Integer(grainField.getRipeness());
	}


	//
	// Entity2DView
	//

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
	public void entityChanged(Entity entity, Object property)
	{
		super.entityChanged(entity, property);

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		} else if(property == GrainField.PROP_RIPENESS) {
			stateChanged = true;
		}
	}
}
