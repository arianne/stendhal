/*
 * @(#) games/stendhal/client/entity/NPC2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of an NPC.
 */
public class NPC2DView extends RPEntity2DView {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(NPC2DView.class);

	/**
	 * Create a 2D view of an NPC.
	 *
	 * @param	entity		The entity to render.
	 */
	public NPC2DView(final RPEntity entity) {
		super(entity);
	}


	//
	// RPEntity2DView
	//

	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @return	A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		SpriteStore store = SpriteStore.get();


		try {
			int code = rpentity.getOutfit();

			if (code != RPEntity.OUTFIT_UNSET) {
				return getOutfitSprite(store, code);
			} else {
				// This NPC's outfit is read from a single file.
				return store.getSprite(translate("npc/" + entity.getEntityClass()));
			}
		} catch (Exception e) {
			logger.error("Cannot build animations", e);
			return store.getSprite(translate(entity.getEntityClass()));
		}
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(Map<Object, Sprite> map) {
		buildSprites(map, 1.5, 2.0);
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
		return new Rectangle.Double(getX(), getY(), 1.5, 2.0);
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
		}
	}
}
