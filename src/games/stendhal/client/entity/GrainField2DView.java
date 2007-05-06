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


import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a grain field.
 */
public class GrainField2DView extends AnimatedStateEntity2DView {
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
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(Map<Object, AnimatedSprite> map) {
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

		String resource = translate(clazz);

		SpriteStore store = SpriteStore.get();

		for(int i = 0; i <= maxRipeness; i++) {
			map.put(Integer.toString(i),
				store.getAnimatedSprite(resource, i, 1, width, height, 0L, false));
		}
	}


	/**
	 * Get the default state name.
	 * <strong>All sub-classes MUST provide a <code>0</code>
	 * named animation, or override this method</strong>.
	 */
	@Override
	protected Object getDefaultState() {
		return "0";
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
}
