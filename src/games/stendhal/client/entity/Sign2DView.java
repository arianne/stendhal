/*
 * @(#) games/stendhal/client/entity/Sign2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * The 2D view of a sign.
 */
public class Sign2DView extends Entity2DView {
	/**
	 * Create a 2D view of a sign.
	 *
	 * @param	entity		The entity to render.
	 */
	public Sign2DView(final Sign sign) {
		super(sign);
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation() {
		String name = getClassResourcePath();

		if (name == null) {
			name = "default";
		}

		setSprite(SpriteStore.get().getSprite(translate(name)));
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
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
		return 5000;
	}


	/**
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param	name		The resource name.
	 *
	 * @return	The full resource name.
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
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property)
	{
		super.entityChanged(entity, property);

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		}
	}
}
